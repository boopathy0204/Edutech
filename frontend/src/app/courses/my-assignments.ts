import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { CommonModule } from '@angular/common';
import { AuthService } from '../auth.service';
import { forkJoin } from 'rxjs';

@Component({
  selector: 'app-my-assignments',
  imports: [CommonModule, RouterLink],
  templateUrl: './my-assignments.html',
  styleUrl: './my-assignments.css'
})
export class MyAssignments implements OnInit {
  courseId!: number;
  course: any = null;
  student: any = null;
  assignments: any[] = [];
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
      const idParam = params.get('courseId');
      if (idParam) {
        this.courseId = +idParam;
        this.loadAssignments();
      }
    });
  }

  loadAssignments() {
    this.loading = true;
    this.error = '';
    this.cdr.detectChanges();

    // Fetch student details to map submissions
    this.authService.getStudentProfile().subscribe({
      next: (studentData) => {
        this.student = studentData;

        // Fetch course assignments & submissions in parallel
        forkJoin({
          assignmentList: this.http.get<any[]>(`/api/assignment/${this.courseId}/course-list`),
          submissionList: this.http.get<any[]>(`/api/submission/${studentData.studentId}/submission-listbystudent`)
        }).subscribe({
          next: ({ assignmentList, submissionList }) => {
            if (assignmentList.length > 0) {
              this.course = {
                courseName: assignmentList[0].courseName
              };
            }

            // Map submission details to each assignment
            this.assignments = assignmentList.map(assignment => {
              // Check if there is a submission for this assignment
              const submission = submissionList.find(sub => sub.assignmentId === assignment.assignmentId);
              
              let status = 'Not Submitted';
              let marksObtained: number | null = null;
              let submissionId: number | null = null;

              if (submission) {
                submissionId = submission.submissionId;
                if (submission.status === 'GRADED') {
                  status = 'Graded';
                } else {
                  status = 'Submitted';
                }
              }

              return {
                ...assignment,
                status,
                marksObtained,
                submissionId
              };
            });

            this.loading = false;
            this.cdr.detectChanges();
          },
          error: (err) => {
            console.error('Failed to load course assignments data:', err);
            this.error = 'Failed to load assignments and submissions data.';
            this.loading = false;
            this.cdr.detectChanges();
          }
        });
      },
      error: (err) => {
        console.error('Failed to load student details:', err);
        this.error = 'Failed to load student details.';
        this.loading = false;
        this.cdr.detectChanges();
      }
    });
  }
}
