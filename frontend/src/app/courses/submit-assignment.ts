import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { CommonModule } from '@angular/common';
import { AuthService } from '../auth.service';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-submit-assignment',
  imports: [CommonModule, RouterLink, FormsModule],
  templateUrl: './submit-assignment.html',
  styleUrl: './submit-assignment.css'
})
export class SubmitAssignment implements OnInit {
  courseId!: number;
  assignmentId!: number;
  assignment: any = null;
  student: any = null;
  selectedFile: File | null = null;
  remarks = '';
  loading = false;
  submitting = false;
  error = '';

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private http: HttpClient,
    private cdr: ChangeDetectorRef,
    private authService: AuthService
  ) {}

  ngOnInit() {
    this.route.paramMap.subscribe(params => {
      const cIdParam = params.get('courseId');
      const aIdParam = params.get('assignmentId');
      if (cIdParam && aIdParam) {
        this.courseId = +cIdParam;
        this.assignmentId = +aIdParam;
        this.loadAssignmentContext();
      }
    });
  }

  loadAssignmentContext() {
    this.loading = true;
    this.error = '';
    this.cdr.detectChanges();

    this.http.get<any>(`/api/assignment/${this.assignmentId}/assignment-details`).subscribe({
      next: (assignmentData) => {
        this.assignment = assignmentData;
        
        this.authService.getStudentProfile().subscribe({
          next: (studentData) => {
            this.student = studentData;
            this.loading = false;
            this.cdr.detectChanges();
          },
          error: (err) => {
            console.error('Failed to load student context details:', err);
            this.error = 'Failed to load student profile details.';
            this.loading = false;
            this.cdr.detectChanges();
          }
        });
      },
      error: (err) => {
        console.error('Failed to load assignment info:', err);
        this.error = 'Assignment details not found or failed to load.';
        this.loading = false;
        this.cdr.detectChanges();
      }
    });
  }

  onFileSelected(event: any) {
    const fileList: FileList = event.target.files;
    if (fileList.length > 0) {
      this.selectedFile = fileList[0];
      this.cdr.detectChanges();
    }
  }

  onSubmit() {
    if (!this.selectedFile) {
      this.error = 'Please choose a file to upload before submitting.';
      return;
    }
    if (!this.student) {
      this.error = 'Student details not loaded. Please reload the page.';
      return;
    }

    this.submitting = true;
    this.error = '';
    this.cdr.detectChanges();

    const formData = new FormData();
    formData.append('file', this.selectedFile);

    // Create a JSON blob for the request metadata
    const requestBlob = new Blob([JSON.stringify({
      studentId: this.student.studentId,
      assignmentId: this.assignmentId,
      remarks: this.remarks.trim()
    })], { type: 'application/json' });

    formData.append('submission', requestBlob);

    this.http.post<any>('/api/submission', formData).subscribe({
      next: () => {
        this.submitting = false;
        this.router.navigate(['/courses', this.courseId, 'assignments', this.assignmentId]);
      },
      error: (err) => {
        console.error('Submission failed:', err);
        this.error = err.error?.message || 'Failed to submit assignment. Make sure the file format is supported.';
        this.submitting = false;
        this.cdr.detectChanges();
      }
    });
  }
}
