import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-professor-notifications',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './notifications.html',
  styleUrl: './notifications.css'
})
export class ProfessorNotifications implements OnInit {
  notificationsList: any[] = [];
  loading = false;
  success = '';

  constructor(
    private http: HttpClient,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit() {
    this.loadNotifications();
  }

  loadNotifications() {
    this.loading = true;
    this.cdr.detectChanges();
    
    // Initialize an empty array to show a clean notification empty state from the database/service
    this.notificationsList = [];
    this.loading = false;
    this.cdr.detectChanges();
  }

  markAsRead(notifId: number) {
    this.notificationsList = this.notificationsList.map(n => {
      if (n.id === notifId) {
        return { ...n, read: true };
      }
      return n;
    });
    this.success = 'Notification marked as read.';
    this.cdr.detectChanges();
    
    setTimeout(() => {
      this.success = '';
      this.cdr.detectChanges();
    }, 2000);
  }

  markAllAsRead() {
    this.notificationsList = this.notificationsList.map(n => ({ ...n, read: true }));
    this.success = 'All notifications marked as read.';
    this.cdr.detectChanges();

    setTimeout(() => {
      this.success = '';
      this.cdr.detectChanges();
    }, 2000);
  }
}
