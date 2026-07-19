import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

import { forkJoin, of } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { AuthService } from '../../auth.service';
import { AcademicPeriodService } from '../../academic-period.service';

@Component({
  selector: 'app-professor-grades',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './grades.html',
  styleUrl: './grades.css'
})
export class ProfessorGrades implements OnInit {
  gradesList: any[] = [];
  filteredGrades: any[] = [];
  courses: any[] = [];
  loading = false;
  saving = false;
  error = '';
  success = '';

  // Summary Metrics
  pendingCount = 0;
  completedCount = 0;

  // Filters
  selectedCourseFilter = '';
  searchQuery = '';
  professorId: number | null = null;
  selectedPeriodId: number | null = null;

  constructor(
    private http: HttpClient,
    private cdr: ChangeDetectorRef,
    private authService: AuthService,
    private academicPeriodService: AcademicPeriodService
  ) {}

  ngOnInit() {
    this.academicPeriodService.selectedPeriod$.subscribe(period => {
      if (period) {
        this.selectedPeriodId = period.academicPeriodId;
        this.loadCourses();
      }
    });
  }

  loadCourses() {
    this.loading = true;
    this.cdr.detectChanges();

    this.authService.getProfessorProfile().subscribe({
      next: (profile) => {
        this.professorId = profile.professorId;
        let url = `/api/course/${profile.professorId}/course-listbyprofessor`;
        if (this.selectedPeriodId) {
          url += `?academicPeriodId=${this.selectedPeriodId}`;
        }
        this.http.get<any[]>(url).subscribe({
          next: (data) => {
            this.courses = data;
            this.loadGrades();
          },
          error: (err) => {
            console.error('Failed to load courses:', err);
            this.loading = false;
            this.cdr.detectChanges();
          }
        });
      },
      error: (err) => {
        console.error('Failed to load professor profile:', err);
        this.error = 'Failed to load professor details.';
        this.loading = false;
        this.cdr.detectChanges();
      }
    });
  }

  loadGrades() {
    if (!this.professorId) return;

    this.loading = true;
    this.error = '';
    this.cdr.detectChanges();

    let url = `/api/grade/grade-center/professor/${this.professorId}`;
    const params: string[] = [];
    if (this.selectedCourseFilter) {
      params.push(`courseId=${this.selectedCourseFilter}`);
    }
    if (this.searchQuery.trim()) {
      params.push(`query=${encodeURIComponent(this.searchQuery.trim())}`);
    }
    if (this.selectedPeriodId) {
      params.push(`academicPeriodId=${this.selectedPeriodId}`);
    }
    if (params.length > 0) {
      url += `?${params.join('&')}`;
    }

    this.http.get<any[]>(url).subscribe({
      next: (data) => {
        this.gradesList = data.map(g => ({
          gradeId: g.gradeId,
          courseId: g.courseId,
          courseCode: g.courseCode,
          courseName: g.courseName,
          assignmentId: g.assignmentId,
          assignmentTitle: g.assignmentTitle,
          marks: g.marks,
          maxMarks: g.maxMarks,
          percentage: g.percentage,
          letterGrade: g.letterGrade,
          feedback: g.feedback,
          studentId: g.studentId,
          studentName: g.studentName,
          regNumber: g.registrationNumber || `STU-${g.studentId}`
        }));
        this.filteredGrades = [...this.gradesList];
        
        // Update completedCount based on initial page load (no active filters)
        if (!this.selectedCourseFilter && !this.searchQuery.trim()) {
          this.completedCount = data.length;
        }

        this.loading = false;
        this.cdr.detectChanges();
      },
      error: (err) => {
        console.error('Failed to load grade center:', err);
        this.error = 'Failed to load grade center records.';
        this.gradesList = [];
        this.filteredGrades = [];
        this.loading = false;
        this.cdr.detectChanges();
      }
    });
  }

  applySearch() {
    this.loadGrades();
  }

  onFilterChange() {
    this.loadGrades();
  }
}
