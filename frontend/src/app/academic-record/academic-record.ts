import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { CommonModule } from '@angular/common';
import { AuthService } from '../auth.service';
import { FormsModule } from '@angular/forms';
import { AcademicPeriodService } from '../academic-period.service';

@Component({
  selector: 'app-academic-record',
  imports: [CommonModule, FormsModule],
  templateUrl: './academic-record.html',
  styleUrl: './academic-record.css'
})
export class AcademicRecord implements OnInit {
  student: any = null;
  record: any = null;
  filteredCourses: any[] = [];
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
        this.loadAcademicRecord();
      }
    });
  }

  loadAcademicRecord() {
    this.loading = true;
    this.error = '';
    this.cdr.detectChanges();

    this.authService.getStudentProfile().subscribe({
      next: (studentData) => {
        this.student = studentData;

        // Fetch academic records
        let url = `/api/academic-records/${studentData.studentId}/student-list`;
        if (this.selectedPeriodId) {
          url += `?academicPeriodId=${this.selectedPeriodId}`;
        }

        this.http.get<any>(url).subscribe({
          next: (recordData) => {
            this.record = recordData;
            this.filteredCourses = recordData ? (recordData.courses || []) : [];
            this.loading = false;
            this.cdr.detectChanges();
          },
          error: (err) => {
            console.error('Failed to load academic records:', err);
            this.error = 'Failed to load your cumulative academic records transcript.';
            this.loading = false;
            this.cdr.detectChanges();
          }
        });
      },
      error: (err) => {
        console.error('Failed to load student context:', err);
        this.error = 'Could not load student profile settings.';
        this.loading = false;
        this.cdr.detectChanges();
      }
    });
  }
}
