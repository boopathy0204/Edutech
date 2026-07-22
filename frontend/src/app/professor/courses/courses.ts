import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { AuthService } from '../../auth.service';
import { AcademicPeriodService } from '../../academic-period.service';

@Component({
  selector: 'app-professor-courses',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './courses.html',
  styleUrl: './courses.css'
})
export class ProfessorCourses implements OnInit {
  courses: any[] = [];
  filteredCourses: any[] = [];
  loading = false;
  saving = false;
  error = '';
  success = '';
  
  // Filters and search states
  searchQuery = '';
  
  // Pagination
  currentPage = 1;
  pageSize = 5;
  totalPages = 1;

  // Modal dialog states
  showModal = false;
  modalTitle = 'Create Course';
  editingCourseId: number | null = null;
  currentProfessorId: number | null = null;
  currentPeriod: any = null;

  // Form Fields
  formCode = '';
  formName = '';
  formDesc = '';
  formCredits = 4;
  formEnrollStart = '';
  formEnrollEnd = '';
  formCourseStart = '';
  formCourseEnd = '';
  formStatus = 'UPCOMING';

  constructor(
    private http: HttpClient,
    private cdr: ChangeDetectorRef,
    private authService: AuthService,
    private academicPeriodService: AcademicPeriodService
  ) {}

  ngOnInit() {
    this.academicPeriodService.selectedPeriod$.subscribe((period: any) => {
      if (period) {
        this.currentPeriod = period;
        this.loadProfessorCourses();
      }
    });
  }

  loadProfessorCourses() {
    this.loading = true;
    this.error = '';
    this.cdr.detectChanges();

    this.authService.getProfessorProfile().subscribe({
      next: (profile) => {
        this.currentProfessorId = profile.professorId;
        this.fetchCoursesFromBackend(profile.professorId, this.searchQuery);
      },
      error: (err) => {
        console.error('Failed to load professor profile:', err);
        this.error = 'Failed to load professor details.';
        this.loading = false;
        this.cdr.detectChanges();
      }
    });
  }

  fetchCoursesFromBackend(professorId: number, query: string) {
    let url = `/api/course/${professorId}/course-listbyprofessor`;
    const params: string[] = [];
    if (query.trim()) {
      params.push(`query=${encodeURIComponent(query.trim())}`);
    }
    if (this.currentPeriod && this.currentPeriod.academicPeriodId) {
      params.push(`academicPeriodId=${this.currentPeriod.academicPeriodId}`);
    }
    if (params.length > 0) {
      url += `?${params.join('&')}`;
    }

    this.http.get<any[]>(url).subscribe({
      next: (data) => {
        this.courses = data.map((c, index) => ({
          ...c,
          studentsCount: 0, 
          assignmentsCount: 0
        }));
        this.applyFilters();
        this.loading = false;
        this.cdr.detectChanges();
      },
      error: (err) => {
        console.error('Failed to load professor courses:', err);
        this.error = 'Failed to load courses list. Please try again.';
        this.loading = false;
        this.cdr.detectChanges();
      }
    });
  }

  applyFilters() {
    let result = [...this.courses];

    this.totalPages = Math.ceil(result.length / this.pageSize) || 1;
    const startIndex = (this.currentPage - 1) * this.pageSize;
    this.filteredCourses = result.slice(startIndex, startIndex + this.pageSize);
    this.cdr.detectChanges();
  }

  onSearch() {
    this.currentPage = 1;
    if (this.currentProfessorId) {
      this.fetchCoursesFromBackend(this.currentProfessorId, this.searchQuery);
    }
  }

  clearSearch() {
    this.searchQuery = '';
    this.currentPage = 1;
    if (this.currentProfessorId) {
      this.fetchCoursesFromBackend(this.currentProfessorId, '');
    }
  }

  changePage(page: number) {
    if (page >= 1 && page <= this.totalPages) {
      this.currentPage = page;
      this.applyFilters();
    }
  }

  openCreateModal() {
    this.error = '';
    this.success = '';
    this.modalTitle = 'Create Course';
    this.editingCourseId = null;
    this.formCode = '';
    this.formName = '';
    this.formDesc = '';
    this.formCredits = 4;
    this.formEnrollStart = '';
    this.formEnrollEnd = '';
    this.formCourseStart = '';
    this.formCourseEnd = '';
    this.formStatus = 'UPCOMING';
    this.showModal = true;
    this.cdr.detectChanges();
  }

  openEditModal(course: any) {
    this.error = '';
    this.success = '';
    this.modalTitle = 'Edit Course';
    this.editingCourseId = course.courseId;
    this.formCode = course.courseCode;
    this.formName = course.courseName;
    this.formDesc = course.description || '';
    this.formCredits = course.credits;
    this.formEnrollStart = course.enrollmentStartDate || '';
    this.formEnrollEnd = course.enrollmentEndDate || '';
    this.formCourseStart = course.courseStartDate || '';
    this.formCourseEnd = course.courseEndDate || '';
    this.formStatus = course.courseStatus || 'UPCOMING';
    this.showModal = true;
    this.cdr.detectChanges();
  }

  closeModal() {
    this.error = '';
    this.showModal = false;
    this.cdr.detectChanges();
  }

  completeCourse(courseId: number) {
    if (!confirm('Are you sure you want to mark this course as COMPLETED? This will freeze the course, calculate final student grades, and store them permanently in their Academic Records.')) {
      return;
    }
    this.loading = true;
    this.error = '';
    this.success = '';
    this.cdr.detectChanges();
    this.http.put(`/api/course/${courseId}/complete`, {}, { responseType: 'text' }).subscribe({
      next: () => {
        this.success = 'Course marked as COMPLETED successfully!';
        this.loadProfessorCourses();
      },
      error: (err) => {
        console.error('Failed to complete course:', err);
        this.error = err.error?.message || 'Failed to complete course.';
        this.loading = false;
        this.cdr.detectChanges();
      }
    });
  }

  saveCourse() {
    if (!this.formCode.trim() || !this.formName.trim() || !this.currentProfessorId) {
      this.error = 'Please fill out all required fields.';
      return;
    }

    this.saving = true;
    this.error = '';
    this.success = '';
    this.cdr.detectChanges();

    const payload = {
      courseCode: this.formCode.trim(),
      courseName: this.formName.trim(),
      description: this.formDesc.trim(),
      credits: Number(this.formCredits),
      professorId: Number(this.currentProfessorId),
      enrollmentStartDate: this.formEnrollStart || null,
      enrollmentEndDate: this.formEnrollEnd || null,
      courseStartDate: this.formCourseStart || null,
      courseEndDate: this.formCourseEnd || null
    };

    if (this.editingCourseId) {
      this.http.put(`/api/course/${this.editingCourseId}/update-course`, payload, { responseType: 'text' }).subscribe({
        next: () => {
          this.success = 'Course updated successfully!';
          this.saving = false;
          this.showModal = false;
          this.cdr.detectChanges();
          try {
            this.loadProfessorCourses();
          } catch (e) {
            console.error('Failed to reload courses after update:', e);
          }
        },
        error: (err) => {
          console.error('Update failed:', err);
          if (Array.isArray(err.error)) {
            this.error = err.error.join(', ');
          } else {
            this.error = (typeof err.error === 'string') ? err.error : (err.error?.message || 'Failed to update course in the database.');
          }
          this.saving = false;
          this.cdr.detectChanges();
        }
      });
    } else {
      this.http.post<any>('/api/course', payload).subscribe({
        next: () => {
          this.success = 'Course created successfully!';
          this.saving = false;
          this.showModal = false;
          this.cdr.detectChanges();
          try {
            this.loadProfessorCourses();
          } catch (e) {
            console.error('Failed to reload courses after create:', e);
          }
        },
        error: (err) => {
          console.error('Create failed:', err);
          if (Array.isArray(err.error)) {
            this.error = err.error.join(', ');
          } else {
            this.error = (typeof err.error === 'string') ? err.error : (err.error?.message || 'Failed to create course in the database.');
          }
          this.saving = false;
          this.cdr.detectChanges();
        }
      });
    }
  }

  deleteCourse(courseId: number) {
    if (confirm('Are you sure you want to delete this course permanently? This will delete all course lectures, assignments, and grades.')) {
      this.http.delete(`/api/course/${courseId}/delete-course`, { responseType: 'text' }).subscribe({
        next: () => {
          this.success = 'Course deleted successfully!';
          this.loadProfessorCourses();
        },
        error: (err) => {
          console.error('Delete failed:', err);
          this.error = 'Failed to delete course from the database.';
          this.cdr.detectChanges();
        }
      });
    }
  }
}
