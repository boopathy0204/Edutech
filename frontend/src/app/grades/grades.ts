import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { CommonModule } from '@angular/common';
import { AuthService } from '../auth.service';
import { AcademicPeriodService } from '../academic-period.service';

@Component({
  selector: 'app-grades',
  imports: [CommonModule],
  templateUrl: './grades.html',
  styleUrl: './grades.css'
})
export class MyGrades implements OnInit {
  student: any = null;
  grades: any[] = [];
  loading = false;
  error = '';
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
        this.loadStudentGrades();
      }
    });
  }

  loadStudentGrades() {
    this.loading = true;
    this.error = '';
    this.cdr.detectChanges();

    this.authService.getStudentProfile().subscribe({
      next: (studentData) => {
        this.student = studentData;

        // Fetch grades for this student
        let url = `/api/grade/${studentData.studentId}/student-list`;
        if (this.selectedPeriodId) {
          url += `?academicPeriodId=${this.selectedPeriodId}`;
        }

        this.http.get<any[]>(url).subscribe({
          next: (gradesList) => {
            this.grades = gradesList;
            this.loading = false;
            this.cdr.detectChanges();
          },
          error: (err) => {
            console.error('Failed to load student grades:', err);
            this.error = 'Failed to load your grades catalog.';
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
}
