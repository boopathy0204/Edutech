import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { forkJoin, of } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { AuthService } from '../../auth.service';
import { AcademicPeriodService } from '../../academic-period.service';

@Component({
  selector: 'app-professor-submissions',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './submissions.html',
  styleUrl: './submissions.css'
})
export class ProfessorSubmissions implements OnInit {
  submissions: any[] = [];
  filteredSubmissions: any[] = [];
  courses: any[] = [];
  loading = false;
  saving = false;
  error = '';
  success = '';

  // Pagination & Filtering
  searchQuery = '';
  statusFilter = '';
  currentPage = 1;
  pageSize = 10;
  totalPages = 1;
  professorId: number | null = null;
  selectedPeriodId: number | null = null;

  // Grade Modal State
  showGradeModal = false;
  selectedSubmission: any = null;
  
  // Grade Form Inputs
  formMarks: number = 0;
  formFeedback = '';
  maxMarks = 100;

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
          next: (courseData) => {
            this.courses = courseData;
            this.loadSubmissions();
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

  loadSubmissions() {
    if (!this.professorId) return;

    this.loading = true;
    this.error = '';
    this.cdr.detectChanges();

    let url = `/api/submission/professor/${this.professorId}/submission-center`;
    const params: string[] = [];
    if (this.statusFilter) {
      params.push(`status=${this.statusFilter}`);
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
        this.submissions = data.map(sub => ({
          submissionId: sub.submissionId,
          studentId: sub.studentId,
          studentName: sub.studentName || 'Student',
          regNumber: sub.registrationNumber || `STU-${sub.studentId}`,
          assignmentTitle: sub.assignmentTitle,
          courseCode: sub.courseCode || '',
          assignmentId: sub.assignmentId,
          submittedDate: sub.submittedAt,
          status: sub.status,
          fileName: sub.fileName,
          marks: sub.marks,
          feedback: sub.feedback || '',
          gradeId: null // We don't need edit grade from this view anymore
        }));

        this.applyFilters();
        this.loading = false;
        this.cdr.detectChanges();
      },
      error: (err) => {
        console.error('Failed to load submissions:', err);
        this.error = 'Failed to load student submissions.';
        this.submissions = [];
        this.filteredSubmissions = [];
        this.loading = false;
        this.cdr.detectChanges();
      }
    });
  }

  applyFilters() {
    const result = [...this.submissions];
    this.totalPages = Math.ceil(result.length / this.pageSize) || 1;
    const startIndex = (this.currentPage - 1) * this.pageSize;
    this.filteredSubmissions = result.slice(startIndex, startIndex + this.pageSize);
    this.cdr.detectChanges();
  }

  applySearch() {
    this.currentPage = 1;
    this.loadSubmissions();
  }

  onSearch() {
    this.currentPage = 1;
    this.loadSubmissions();
  }

  changePage(page: number) {
    if (page >= 1 && page <= this.totalPages) {
      this.currentPage = page;
      this.applyFilters();
    }
  }

  openGradeModal(sub: any) {
    this.selectedSubmission = sub;
    this.formMarks = sub.marks !== null ? sub.marks : 0;
    this.formFeedback = sub.feedback || '';
    
    // Query max marks from the matching submission assignment reference
    this.http.get<any>(`/api/assignment/${sub.assignmentId}/assignment-details`).subscribe({
      next: (assignmentData) => {
        this.maxMarks = assignmentData.maxMarks || 100;
        this.showGradeModal = true;
        this.cdr.detectChanges();
      },
      error: () => {
        this.maxMarks = 100;
        this.showGradeModal = true;
        this.cdr.detectChanges();
      }
    });
  }

  closeGradeModal() {
    this.showGradeModal = false;
    this.cdr.detectChanges();
  }

  saveGrade() {
    if (this.formMarks < 0 || this.formMarks > this.maxMarks) {
      alert(`Marks must be between 0 and ${this.maxMarks}.`);
      return;
    }

    this.saving = true;
    this.error = '';
    this.success = '';
    this.cdr.detectChanges();

    const payload = {
      submissionId: this.selectedSubmission.submissionId,
      marks: Number(this.formMarks),
      feedback: this.formFeedback.trim()
    };

    // If gradeId exists, we perform a delete on it first to bypass existsBySubmission DuplicateResourceException constraint on creation!
    if (this.selectedSubmission.gradeId) {
      this.http.delete(`/api/grade/${this.selectedSubmission.gradeId}/delete-grade`, { responseType: 'text' }).subscribe({
        next: () => {
          this.createNewGrade(payload);
        },
        error: (err) => {
          console.error('Failed to clear old grade for update:', err);
          this.createNewGrade(payload);
        }
      });
    } else {
      this.createNewGrade(payload);
    }
  }

  createNewGrade(payload: any) {
    this.http.post<any>('/api/grade', payload).subscribe({
      next: () => {
        this.success = 'Assignment graded successfully!';
        this.loadSubmissions();
        this.saving = false;
        this.showGradeModal = false;
      },
      error: (err) => {
        console.error('Failed to evaluate grade:', err);
        this.error = err.error?.message || 'Failed to submit grade details.';
        this.saving = false;
        this.cdr.detectChanges();
      }
    });
  }

  downloadSubmission(submissionId: number, fileName: string) {
    this.http.get(`/api/submission/${submissionId}/download-submission`, { responseType: 'blob' }).subscribe({
      next: (blob) => {
        const url = window.URL.createObjectURL(blob);
        const a = document.createElement('a');
        a.href = url;
        a.download = fileName || 'submission';
        document.body.appendChild(a);
        a.click();
        document.body.removeChild(a);
        window.URL.revokeObjectURL(url);
      },
      error: (err) => {
        console.error('Download failed:', err);
      }
    });
  }
}
