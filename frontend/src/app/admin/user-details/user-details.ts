import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-user-details',
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './user-details.html',
  styleUrl: './user-details.css'
})
export class UserDetails implements OnInit {
  userId: number | null = null;
  user: any = null;
  loading = false;
  error = '';

  // Profiles resolved from backend dashboard payload
  studentProfile: any = null;
  professorProfile: any = null;
  adminStaffProfile: any = null;

  // Student specific dashboard variables
  cgpa: number | null = null;
  courses: any[] = [];
  studentAssignments: any[] = [];
  studentStats = {
    totalAssignments: 0,
    completedAssignments: 0,
    pendingAssignments: 0,
    overdueAssignments: 0,
    gradedAssignments: 0,
    awaitingGrading: 0
  };

  // Professor specific dashboard variables
  professorStats = {
    totalCourses: 0,
    totalAssignments: 0,
    gradedAssignments: 0,
    awaitingGrading: 0
  };
  professorCourses: any[] = [];
  professorAssignments: any[] = [];

  // General audit logs & recent activities list
  auditLogs: any[] = [];
  recentActivities: any[] = [];
  loginHistory: any[] = [];

  // Modal controls
  showEditModal = false;
  editUsername = '';
  editEmail = '';
  editFormError = '';
  editFormSaving = false;

  // Audit Logs modal control
  showAuditLogsModal = false;

  // Password reset modal control
  showPasswordModal = false;
  generatedPassword = '';
  copied = false;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private http: HttpClient,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit() {
    this.route.paramMap.subscribe(params => {
      const idStr = params.get('userId');
      if (idStr) {
        this.userId = +idStr;
        this.loadDashboardData();
      }
    });
  }

  loadDashboardData() {
    if (!this.userId) return;
    this.loading = true;
    this.error = '';
    this.cdr.detectChanges();

    // Directly call the backend calculations endpoint created in the dashboard package
    this.http.get<any>(`/api/dashboard/${this.userId}/user-details`).subscribe({
      next: (dashboardData) => {
        this.user = dashboardData.account;
        
        // Resolve profiles
        this.studentProfile = dashboardData.studentProfile;
        this.professorProfile = dashboardData.professorProfile;
        
        // Populate stats, courses, and assignments
        if (this.user.role === 'STUDENT') {
          this.studentStats = dashboardData.statistics || this.studentStats;
          this.courses = dashboardData.courses || [];
          this.studentAssignments = dashboardData.assignments || [];

        } else if (this.user.role === 'PROFESSOR') {
          this.professorStats = dashboardData.statistics || this.professorStats;
          this.professorCourses = dashboardData.courses || [];
          this.professorAssignments = dashboardData.assignments || [];
        }

        // Load timeline and session data
        this.generateMockActivitiesAndSessions();
        this.loadAuditLogsFromStorage();
        this.loading = false;
        this.cdr.detectChanges();
      },
      error: (err) => {
        console.error('Failed to load user dashboard:', err);
        const backendMessage = typeof err.error === 'string' ? err.error : (err.error?.message || 'Failed to load user account dashboard.');
        
        // Fallback: Check if it's an admin user that has no dashboard service calculations in switch block
        this.http.get<any>(`/api/user/${this.userId}/user-details`).subscribe({
          next: (userData) => {
            this.user = userData;
            if (this.user.role === 'ADMIN') {
              this.loadAdminStaffData();
            } else {
              this.error = backendMessage;
              this.loading = false;
              this.cdr.detectChanges();
            }
          },
          error: () => {
            this.error = backendMessage;
            this.loading = false;
            this.cdr.detectChanges();
          }
        });
      }
    });
  }

  loadAdminStaffData() {
    this.http.get<any[]>('/api/admin-staff').subscribe({
      next: (staffList) => {
        this.adminStaffProfile = staffList.find(s => s.userId === this.userId);
        if (!this.adminStaffProfile) {
          this.error = 'AdminStaff not found';
          this.loading = false;
          this.cdr.detectChanges();
          return;
        }
        this.generateMockActivitiesAndSessions();
        this.loadAuditLogsFromStorage();
        this.loading = false;
        this.cdr.detectChanges();
      },
      error: (err) => {
        console.error('Failed to load admin staff list:', err);
        this.error = 'Failed to load admin staff details.';
        this.loading = false;
        this.cdr.detectChanges();
      }
    });
  }

  generateMockActivitiesAndSessions() {
    // Generate simulated login history for visualization
    this.loginHistory = [
      { timestamp: new Date(Date.now() - 3600000 * 2).toISOString(), ipAddress: '192.168.1.45', device: 'Chrome / macOS', location: 'Campus Library' },
      { timestamp: new Date(Date.now() - 3600000 * 24).toISOString(), ipAddress: '192.168.1.101', device: 'Safari / iPhone iOS', location: 'Campus Wi-Fi' },
      { timestamp: new Date(Date.now() - 3600000 * 48).toISOString(), ipAddress: '203.0.113.12', device: 'Firefox / Windows 11', location: 'External Network' }
    ];

    // Populate recent academic logs depending on calculations lists
    this.recentActivities = [];
    if (this.user?.role === 'STUDENT') {
      this.studentAssignments.forEach(a => {
        if (a.submissionDate) {
          this.recentActivities.push({
            type: 'submission',
            title: 'Assignment Submitted',
            description: `Submitted homework folder for "${a.assignmentTitle}" in course ${a.courseName}.`,
            timestamp: a.submissionDate
          });
        }
      });
    } else if (this.user?.role === 'PROFESSOR') {
      this.professorAssignments.forEach(a => {
        this.recentActivities.push({
          type: 'grading',
          title: 'Assignment Published',
          description: `Published class task: "${a.assignmentTitle}" for course ${a.courseName}.`,
          timestamp: a.dueDate
        });
      });
    }
  }

  loadAuditLogsFromStorage() {
    const logs = JSON.parse(localStorage.getItem('audit_logs') || '[]');
    this.auditLogs = logs.filter((log: any) => log.userId === this.userId || log.targetUsername === this.user?.username);
  }

  addAuditLog(action: string, details: string) {
    const logs = JSON.parse(localStorage.getItem('audit_logs') || '[]');
    const currentAdminUser = JSON.parse(sessionStorage.getItem('auth_user') || '{}');
    const newLog = {
      timestamp: new Date().toISOString(),
      action: action,
      details: details,
      userId: this.userId,
      targetUsername: this.user.username,
      performedBy: currentAdminUser.username || 'Administrator'
    };
    logs.unshift(newLog);
    localStorage.setItem('audit_logs', JSON.stringify(logs));
    this.loadAuditLogsFromStorage();
  }

  toggleEnable() {
    if (!this.user) return;
    
    const newStatus = !this.user.enabled;
    const payload = {
      username: this.user.username,
      email: this.user.email,
      role: this.user.role,
      enabled: newStatus
    };

    this.http.put(`/api/user/${this.userId}/update-user`, payload, { responseType: 'text' }).subscribe({
      next: () => {
        this.user.enabled = newStatus;
        this.addAuditLog(
          newStatus ? 'ACCOUNT_ENABLED' : 'ACCOUNT_DISABLED', 
          `Administrator changed active status to: ${newStatus}`
        );
        this.cdr.detectChanges();
      },
      error: (err) => console.error('Failed to update enabled status:', err)
    });
  }

  toggleLock() {
    if (!this.user) return;

    const newStatus = !this.user.accountLocked;
    const payload = {
      username: this.user.username,
      email: this.user.email,
      role: this.user.role,
      accountLocked: newStatus
    };

    this.http.put(`/api/user/${this.userId}/update-user`, payload, { responseType: 'text' }).subscribe({
      next: () => {
        this.user.accountLocked = newStatus;
        this.addAuditLog(
          newStatus ? 'ACCOUNT_LOCKED' : 'ACCOUNT_UNLOCKED', 
          `Administrator changed lock status to: ${newStatus}`
        );
        this.cdr.detectChanges();
      },
      error: (err) => console.error('Failed to update locked status:', err)
    });
  }

  resetPassword() {
    if (!this.user) return;

    const chars = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*';
    let generatedPass = '';
    for (let i = 0; i < 10; i++) {
      generatedPass += chars.charAt(Math.floor(Math.random() * chars.length));
    }

    const payload = {
      username: this.user.username,
      email: this.user.email,
      role: this.user.role,
      password: generatedPass
    };

    this.http.put(`/api/user/${this.userId}/update-user`, payload, { responseType: 'text' }).subscribe({
      next: () => {
        this.generatedPassword = generatedPass;
        this.copied = false;
        this.showPasswordModal = true;
        this.addAuditLog('PASSWORD_CHANGED', 'Administrator triggered forced password reset.');
        this.cdr.detectChanges();
      },
      error: (err) => console.error('Password reset failed:', err)
    });
  }

  copyPassword() {
    navigator.clipboard.writeText(this.generatedPassword).then(() => {
      this.copied = true;
      this.cdr.detectChanges();
      setTimeout(() => {
        this.copied = false;
        this.cdr.detectChanges();
      }, 2000);
    });
  }

  closePasswordModal() {
    this.showPasswordModal = false;
    this.generatedPassword = '';
    this.copied = false;
    this.cdr.detectChanges();
  }

  openEdit() {
    this.editUsername = this.user.username;
    this.editEmail = this.user.email;
    this.editFormError = '';
    this.showEditModal = true;
  }

  closeEdit() {
    this.showEditModal = false;
  }

  saveEdit() {
    this.editFormError = '';
    if (!this.editUsername.trim() || !this.editEmail.trim()) {
      this.editFormError = 'Username and Email are required.';
      return;
    }

    this.editFormSaving = true;
    const payload = {
      username: this.editUsername.trim(),
      email: this.editEmail.trim(),
      role: this.user.role
    };

    this.http.put(`/api/user/${this.userId}/update-user`, payload, { responseType: 'text' }).subscribe({
      next: () => {
        this.user.username = payload.username;
        this.user.email = payload.email;
        this.addAuditLog('USER_UPDATED', `Account updated - Username: ${payload.username}, Email: ${payload.email}`);
        this.editFormSaving = false;
        this.closeEdit();
        this.cdr.detectChanges();
      },
      error: (err) => {
        console.error('Update failed:', err);
        this.editFormError = err.error || 'Failed to update user account settings.';
        this.editFormSaving = false;
        this.cdr.detectChanges();
      }
    });
  }

  openAuditLogs() {
    this.showAuditLogsModal = true;
  }

  closeAuditLogs() {
    this.showAuditLogsModal = false;
  }
}
