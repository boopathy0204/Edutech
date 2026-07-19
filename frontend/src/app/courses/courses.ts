import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { AuthService } from '../auth.service';
import { AcademicPeriodService } from '../academic-period.service';

@Component({
  selector: 'app-courses',
  imports: [CommonModule, RouterLink, FormsModule],
  templateUrl: './courses.html',
  styleUrl: './courses.css'
})
export class MyCourses implements OnInit {
  student: any = null;
  courses: any[] = [];
  loading = false;
  error = '';
  searchQuery = '';
  selectedPeriodId: number | null = null;

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
        this.loadStudentCourses();
      }
    });
  }

  loadStudentCourses(query?: string) {
    this.loading = true;
    this.error = '';
    this.cdr.detectChanges();

    this.authService.getStudentProfile().subscribe({
      next: (studentData) => {
        this.student = studentData;
        
        // Fetch enrollments
        let url = `/api/enrollment/${studentData.studentId}/course-listbystudent`;
        const params: string[] = [];
        if (query && query.trim() !== '') {
          params.push(`query=${encodeURIComponent(query.trim())}`);
        }
        if (this.selectedPeriodId) {
          params.push(`academicPeriodId=${this.selectedPeriodId}`);
        }
        if (params.length > 0) {
          url += `?${params.join('&')}`;
        }

        this.http.get<any[]>(url).subscribe({
          next: (courses) => {
            this.courses = courses;
            this.loading = false;
            this.cdr.detectChanges();
          },
          error: (err) => {
            console.error('Failed to load enrollments:', err);
            this.error = 'Failed to load enrolled courses list.';
            this.loading = false;
            this.cdr.detectChanges();
          }
        });
      },
      error: (err) => {
        console.error('Failed to load student settings profile:', err);
        this.error = 'Could not retrieve student academic profile details.';
        this.loading = false;
        this.cdr.detectChanges();
      }
    });
  }

  applySearch() {
    this.loadStudentCourses(this.searchQuery);
  }

  clearSearch() {
    this.searchQuery = '';
    this.loadStudentCourses();
  }
}
