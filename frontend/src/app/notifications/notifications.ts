import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { CommonModule } from '@angular/common';
import { AuthService } from '../auth.service';
import { AcademicPeriodService } from '../academic-period.service';
import { forkJoin } from 'rxjs';

@Component({
  selector: 'app-notifications',
  imports: [CommonModule],
  templateUrl: './notifications.html',
  styleUrl: './notifications.css'
})
export class MyNotifications implements OnInit {
  student: any = null;
  notifications: any[] = [];
  loading = false;
  error = '';
  markingId: number | null = null;
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
        this.loadNotifications();
      }
    });
  }

  loadNotifications() {
    this.loading = true;
    this.error = '';
    this.cdr.detectChanges();

    this.authService.getStudentProfile().subscribe({
      next: (studentData) => {
        this.student = studentData;

        // Fetch notifications list
        let url = `/api/notification/${studentData.studentId}/notification-listbystudent`;
        if (this.selectedPeriodId) {
          url += `?academicPeriodId=${this.selectedPeriodId}`;
        }
        this.http.get<any[]>(url).subscribe({
          next: (notificationsList) => {
            // Sort notifications by date descending
            this.notifications = notificationsList.sort((a, b) => {
              return new Date(b.createdAt).getTime() - new Date(a.createdAt).getTime();
            });
            this.loading = false;
            this.cdr.detectChanges();
          },
          error: (err) => {
            console.error('Failed to load student notifications:', err);
            this.error = 'Failed to load notifications list.';
            this.loading = false;
            this.cdr.detectChanges();
          }
        });
      },
      error: (err) => {
        console.error('Failed to load student context:', err);
        this.error = 'Could not retrieve student academic profile details.';
        this.loading = false;
        this.cdr.detectChanges();
      }
    });
  }

  markAsRead(notificationId: number) {
    this.markingId = notificationId;
    this.cdr.detectChanges();

    this.http.put(`/api/notification/${notificationId}/read`, {}, { responseType: 'text' }).subscribe({
      next: () => {
        // Update local status
        const notification = this.notifications.find(n => n.notificationId === notificationId);
        if (notification) {
          notification.isRead = true;
        }
        this.markingId = null;
        this.cdr.detectChanges();
      },
      error: (err) => {
        console.error('Failed to mark notification as read:', err);
        this.markingId = null;
        this.cdr.detectChanges();
      }
    });
  }

  markAllAsRead() {
    const unreadNotifications = this.notifications.filter(n => !n.isRead);
    if (unreadNotifications.length === 0) return;

    this.loading = true;
    this.cdr.detectChanges();

    const requests = unreadNotifications.map(n =>
      this.http.put(`/api/notification/${n.notificationId}/read`, {}, { responseType: 'text' })
    );

    forkJoin(requests).subscribe({
      next: () => {
        this.notifications.forEach(n => {
          n.isRead = true;
        });
        this.loading = false;
        this.cdr.detectChanges();
      },
      error: (err) => {
        console.error('Failed to mark all notifications as read:', err);
        this.loading = false;
        this.cdr.detectChanges();
      }
    });
  }

  get unreadCount(): number {
    return this.notifications.filter(n => !n.isRead).length;
  }
}
