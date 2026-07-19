import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { CommonModule } from '@angular/common';
import { AuthService } from '../auth.service';

@Component({
  selector: 'app-assignment-details',
  imports: [CommonModule, RouterLink],
  templateUrl: './assignment-details.html',
  styleUrl: './assignment-details.css'
})
export class AssignmentDetails implements OnInit {
  courseId!: number;
  assignmentId!: number;
  assignment: any = null;
  submission: any = null;
  loading = false;
  error = '';

  constructor(
    private route: ActivatedRoute,
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
        this.loadAssignmentDetails();
      }
    });
  }

  loadAssignmentDetails() {
    this.loading = true;
    this.error = '';
    this.cdr.detectChanges();

    // 1. Fetch assignment details
    this.http.get<any>(`/api/assignment/${this.assignmentId}/assignment-details`).subscribe({
      next: (assignmentData) => {
        this.assignment = assignmentData;

        // 2. Fetch student details to get submissions
        this.authService.getStudentProfile().subscribe({
          next: (studentData) => {
            
            // 3. Fetch submissions for this student
            this.http.get<any[]>(`/api/submission/${studentData.studentId}/submission-listbystudent`).subscribe({
              next: (submissions) => {
                // Find submission for this assignment
                this.submission = submissions.find(sub => sub.assignmentId === this.assignmentId);
                
                if (this.submission && this.submission.status === 'GRADED') {
                  // Fetch student grades to map details
                  this.http.get<any[]>(`/api/grade/${studentData.studentId}/student-list`).subscribe({
                    next: (grades) => {
                      const grade = grades.find(g => g.assignmentId === this.assignmentId);
                      if (grade) {
                        this.submission.grade = grade;
                      }
                      this.loading = false;
                      this.cdr.detectChanges();
                    },
                    error: (err) => {
                      console.error('Failed to load grade details:', err);
                      this.loading = false;
                      this.cdr.detectChanges();
                    }
                  });
                } else {
                  this.loading = false;
                  this.cdr.detectChanges();
                }
              },
              error: (err) => {
                console.error('Failed to load student submissions:', err);
                this.loading = false;
                this.cdr.detectChanges();
              }
            });
          },
          error: (err) => {
            console.error('Failed to load student details:', err);
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
