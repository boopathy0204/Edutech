import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { Router } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AuthService } from '../auth.service';
import { forkJoin, of } from 'rxjs';
import { catchError } from 'rxjs/operators';

import { AcademicPeriodService } from '../academic-period.service';

@Component({
  selector: 'app-dashboard',
  imports: [CommonModule, FormsModule],
  templateUrl: './dashboard.html',
  styleUrl: './dashboard.css',
})
export class Dashboard implements OnInit {
  role = '';
  username = '';
  loading = false;
  error = '';
  selectedPeriodId: number | null = null;

  // Admin Data
  adminStats: any = null;
  adminCourses: any[] = [];
  adminAcademicProgress: any[] = [];

  currentPeriod: any = null;
  transitioning = false;
  transitionError = '';
  transitionSuccess = '';

  constructor(
    private authService: AuthService,
    private router: Router,
    private http: HttpClient,
    private cdr: ChangeDetectorRef,
    private academicPeriodService: AcademicPeriodService
  ) {}

  ngOnInit() {
    this.role = this.authService.getRole() || '';
    this.username = this.authService.getUsername() || '';
    if (this.role === 'STUDENT') {
      this.router.navigate(['/profile']);
      return;
    }
    
    // Fetch active term configuration
    this.http.get<any>('/api/admin-staff/current-period').subscribe({
      next: (p) => {
        this.currentPeriod = p;
        this.cdr.detectChanges();
      },
      error: (err) => console.error('Failed to load current academic period:', err)
    });

    // Subscribe to selection changes
    this.academicPeriodService.selectedPeriod$.subscribe(period => {
      if (period) {
        this.selectedPeriodId = period.academicPeriodId;
        this.loadDashboardData();
      }
    });
  }

  loadDashboardData() {
    this.loading = true;
    this.error = '';

    if (this.role === 'ADMIN') {
      let suffix = '';
      if (this.selectedPeriodId) {
        suffix = `?academicPeriodId=${this.selectedPeriodId}`;
      }

      this.http.get<any>(`/api/report/student-performance${suffix}`).subscribe({
        next: (stats) => {
          this.adminStats = stats;
          this.http.get<any[]>(`/api/report/course-participation${suffix}`).subscribe({
            next: (courses) => {
              this.adminCourses = courses;
              
              // Load Academic Progress report directly from backend Reports API
              this.http.get<any[]>(`/api/report/academic-progress${suffix}`).subscribe({
                next: (progress) => {
                  this.adminAcademicProgress = progress || [];
                  this.loading = false;
                  this.cdr.detectChanges();
                },
                error: (err) => {
                  console.error('Failed to load academic progress report:', err);
                  this.loading = false;
                  this.cdr.detectChanges();
                }
              });
            },
            error: (err) => {
              console.error('Failed to load courses:', err);
              this.error = 'Failed to load course participation data.';
              this.loading = false;
              this.cdr.detectChanges();
            }
          });
        },
        error: (err) => {
          console.error('Failed to load stats:', err);
          this.error = 'Failed to load system performance metrics.';
          this.loading = false;
          this.cdr.detectChanges();
        }
      });
    } else {
      this.loading = false;
      this.cdr.detectChanges();
    }
  }

  advancePeriod() {
    if (!confirm('Are you sure you want to advance the academic period? This will complete all active courses, persist final student grades, and transition all students to the next term.')) {
      return;
    }
    this.transitioning = true;
    this.transitionError = '';
    this.transitionSuccess = '';
    this.cdr.detectChanges();
    
    this.http.post('/api/admin-staff/advance-period', {}, { responseType: 'text' }).subscribe({
      next: () => {
        this.transitionSuccess = 'Academic period advanced successfully!';
        this.transitioning = false;
        this.academicPeriodService.triggerPeriodsRefresh();
        this.loadDashboardData();
        this.http.get<any>('/api/admin-staff/current-period').subscribe({
          next: (p) => {
            this.currentPeriod = p;
            this.cdr.detectChanges();
          }
        });
      },
      error: (err) => {
        console.error('Failed to advance period:', err);
        this.transitionError = err.error?.message || 'Failed to advance academic period.';
        this.transitioning = false;
        this.cdr.detectChanges();
      }
    });
  }
}
