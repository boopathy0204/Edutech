import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { forkJoin, of } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { AuthService } from '../../auth.service';

@Component({
  selector: 'app-professor-assignment-submissions',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './assignment-submissions.html',
  styleUrl: './assignment-submissions.css'
})
export class ProfessorAssignmentSubmissions implements OnInit {
  submissions: any[] = [];
  filteredSubmissions: any[] = [];
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

  // Grade Modal State
  showGradeModal = false;
  selectedSubmission: any = null;
  
  // Grade Form Inputs
  formMarks: number = 0;
  formFeedback = '';
  maxMarks = 100;

  assignmentId: number | null = null;
  courseId: number | null = null;
  assignmentTitle = '';

  constructor(
    private http: HttpClient,
    private cdr: ChangeDetectorRef,
    private authService: AuthService,
    private route: ActivatedRoute
  ) {}

  ngOnInit() {
    const routeAssignmentId = this.route.snapshot.paramMap.get('assignmentId');
    if (routeAssignmentId) {
      this.assignmentId = Number(routeAssignmentId);
      this.loadAssignmentDetailsAndSubmissions();
    } else {
      this.error = 'No assignment ID provided.';
    }
  }

  loadAssignmentDetailsAndSubmissions() {
    if (!this.assignmentId) return;
    this.loading = true;
    this.error = '';
    this.cdr.detectChanges();

    // 1. Fetch assignment details
    this.http.get<any>(`/api/assignment/${this.assignmentId}/assignment-details`).subscribe({
      next: (assignment) => {
        this.courseId = assignment.courseId;
        this.assignmentTitle = assignment.title;
        this.maxMarks = assignment.maxMarks || 100;

        // Construct query parameters for backend filtering
        let subUrl = `/api/submission/${this.assignmentId}/submission-listbyassignment`;
        const params: string[] = [];
        if (this.searchQuery.trim()) {
          params.push(`query=${encodeURIComponent(this.searchQuery.trim())}`);
        }
        if (params.length > 0) {
          subUrl += `?${params.join('&')}`;
        }

        // 2. Fetch submissions & grades
        forkJoin({
          subList: this.http.get<any[]>(subUrl).pipe(catchError(() => of([]))),
          gradeList: this.http.get<any[]>(`/api/grade/${this.assignmentId}/assignment-list`).pipe(catchError(() => of([])))
        }).subscribe({
          next: (res) => {
            this.submissions = res.subList.map(sub => {
              const grade = res.gradeList.find(g => g.submissionId === sub.submissionId);
              return {
                submissionId: sub.submissionId,
                studentId: sub.studentId,
                studentName: sub.studentName || 'Student',
                regNumber: sub.registrationNumber || `STU-${sub.studentId}`,
                assignmentTitle: sub.assignmentTitle,
                assignmentId: sub.assignmentId,
                submittedDate: sub.submittedAt,
                status: sub.status,
                fileName: sub.fileName,
                marks: grade ? grade.marks : null,
                percentage: grade ? grade.percentage : null,
                letterGrade: grade ? grade.letterGrade : null,
                feedback: grade ? grade.feedback : '',
                gradeId: grade ? grade.gradeId : null
              };
            });

            this.applyFilters();
            this.loading = false;
            this.cdr.detectChanges();
          },
          error: (err) => {
            console.error('Failed to load submissions and grades detail lists:', err);
            this.error = 'Failed to load submissions list.';
            this.loading = false;
            this.cdr.detectChanges();
          }
        });
      },
      error: (err) => {
        console.error('Failed to load assignment info:', err);
        this.error = 'Failed to retrieve assignment details.';
        this.loading = false;
        this.cdr.detectChanges();
      }
    });
  }

  applyFilters() {
    let result = [...this.submissions];

    this.totalPages = Math.ceil(result.length / this.pageSize) || 1;
    const startIndex = (this.currentPage - 1) * this.pageSize;
    this.filteredSubmissions = result.slice(startIndex, startIndex + this.pageSize);
    this.cdr.detectChanges();
  }

  onSearch() {
    this.currentPage = 1;
    this.loadAssignmentDetailsAndSubmissions();
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
    this.showGradeModal = true;
    this.cdr.detectChanges();
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

    // If gradeId exists, delete old grade first
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
        this.loadAssignmentDetailsAndSubmissions();
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
