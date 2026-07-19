import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { forkJoin } from 'rxjs';
import { AuthService } from '../auth.service';
import { AcademicPeriodService } from '../academic-period.service';

@Component({
  selector: 'app-course-enrollment',
  imports: [CommonModule, FormsModule],
  templateUrl: './course-enrollment.html',
  styleUrl: './course-enrollment.css'
})
export class CourseEnrollment implements OnInit {
  student: any = null;
  courses: any[] = [];
  allCourses: any[] = [];
  enrolledCourseIds: Set<number> = new Set();
  loading = false;
  enrollingId: number | null = null;
  error = '';
  success = '';
  searchQuery = '';
  filterCredits: number | null = null;
  filterProfessor = '';
  selectedPeriodId: number | null = null;

  selectedCourse: any = null;
  showDetailsModal = false;
  modalScheduleList: any[] = [];

  constructor(
    private http: HttpClient,
    private authService: AuthService,
    private cdr: ChangeDetectorRef,
    private academicPeriodService: AcademicPeriodService
  ) {}

  ngOnInit() {
    this.academicPeriodService.selectedPeriod$.subscribe(period => {
      if (period) {
        this.selectedPeriodId = period.academicPeriodId;
        this.loadEnrollmentCatalog();
      }
    });
  }

  loadEnrollmentCatalog(query?: string) {
    this.loading = true;
    this.error = '';
    this.success = '';
    this.cdr.detectChanges();

    // 1. Fetch student info
    this.authService.getStudentProfile().subscribe({
      next: (studentData) => {
        this.student = studentData;

        // 2. Fetch all courses and student enrollments in parallel
        let courseUrl = '/api/course';
        const courseParams: string[] = [];
        if (query && query.trim() !== '') {
          courseParams.push(`query=${encodeURIComponent(query.trim())}`);
        }
        if (this.selectedPeriodId) {
          courseParams.push(`academicPeriodId=${this.selectedPeriodId}`);
        }
        if (courseParams.length > 0) {
          courseUrl += `?${courseParams.join('&')}`;
        }

        let enrollmentsUrl = `/api/enrollment/${studentData.studentId}/course-listbystudent`;
        if (this.selectedPeriodId) {
          enrollmentsUrl += `?academicPeriodId=${this.selectedPeriodId}`;
        }

        forkJoin({
          allCourses: this.http.get<any[]>(courseUrl),
          myEnrollments: this.http.get<any[]>(enrollmentsUrl)
        }).subscribe({
          next: ({ allCourses, myEnrollments }) => {
            // Store enrolled course IDs for fast lookup
            this.enrolledCourseIds = new Set(myEnrollments.map(e => e.courseId));
            
            // Map enrollment states
            this.allCourses = allCourses.map(course => {
              const enrolled = this.enrolledCourseIds.has(course.courseId);
              const enrollmentDetails = myEnrollments.find(e => e.courseId === course.courseId);
              return {
                ...course,
                isEnrolled: enrolled,
                enrollmentStatus: enrollmentDetails ? enrollmentDetails.status : null
              };
            });

            this.applyLocalFilters();
            this.loading = false;
            this.cdr.detectChanges();
          },
          error: (err) => {
            console.error('Failed to load courses catalog:', err);
            this.error = 'Failed to load course catalogs.';
            this.loading = false;
            this.cdr.detectChanges();
          }
        });
      },
      error: (err) => {
        console.error('Failed to load student context:', err);
        this.error = 'Could not load student profile context.';
        this.loading = false;
        this.cdr.detectChanges();
      }
    });
  }

  applyLocalFilters() {
    let result = [...this.allCourses];
    if (this.searchQuery.trim()) {
      const q = this.searchQuery.toLowerCase().trim();
      result = result.filter(c => 
        c.courseCode?.toLowerCase().includes(q) || 
        c.courseName?.toLowerCase().includes(q)
      );
    }
    if (this.filterCredits != null && String(this.filterCredits).trim() !== '') {
      result = result.filter(c => c.credits === Number(this.filterCredits));
    }
    if (this.filterProfessor.trim()) {
      const q = this.filterProfessor.toLowerCase().trim();
      result = result.filter(c => c.professorName?.toLowerCase().includes(q));
    }
    this.courses = result;
    this.cdr.detectChanges();
  }

  applySearch() {
    this.applyLocalFilters();
  }

  clearSearch() {
    this.searchQuery = '';
    this.filterCredits = null;
    this.filterProfessor = '';
    this.applyLocalFilters();
  }

  enrollInCourse(courseId: number) {
    if (!this.student) return;

    this.enrollingId = courseId;
    this.error = '';
    this.success = '';
    this.cdr.detectChanges();

    const payload = {
      studentId: this.student.studentId,
      courseId: courseId
    };

    this.http.post<any>('/api/enrollment', payload).subscribe({
      next: () => {
        this.success = 'Successfully enrolled in course!';
        this.enrollingId = null;
        this.scrollToTop();
        this.loadEnrollmentCatalog(this.searchQuery); // Refresh listings with current search
      },
      error: (err) => {
        console.error('Enrollment request failed:', err);
        this.error = (typeof err.error === 'string') ? err.error : (err.error?.message || 'Failed to complete course enrollment.');
        this.enrollingId = null;
        this.scrollToTop();
        this.cdr.detectChanges();
      }
    });
  }

  scrollToTop() {
    const element = document.querySelector('.main-content');
    if (element) {
      element.scrollTo({ top: 0, behavior: 'smooth' });
    } else {
      window.scrollTo({ top: 0, behavior: 'smooth' });
    }
  }

  viewCourseDetails(course: any) {
    this.selectedCourse = course;
    this.showDetailsModal = true;
    this.modalScheduleList = [];
    this.cdr.detectChanges();

    this.http.get<any[]>(`/api/schedule/${course.courseId}/schedule-listbycourse`).subscribe({
      next: (data) => {
        this.modalScheduleList = data || [];
        this.cdr.detectChanges();
      },
      error: (err) => {
        console.error('Failed to load course schedule:', err);
      }
    });
  }

  closeDetailsModal() {
    this.showDetailsModal = false;
    this.selectedCourse = null;
    this.cdr.detectChanges();
  }
}
