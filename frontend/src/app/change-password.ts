import { Component, ChangeDetectorRef } from '@angular/core';
import { Router } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { FormsModule } from '@angular/forms';
import { AuthService } from './auth.service';

@Component({
  selector: 'app-change-password',
  imports: [FormsModule],
  templateUrl: './change-password.html',
  styleUrl: './change-password.css'
})
export class ChangePassword {
  oldPassword = '';
  newPassword = '';
  confirmPassword = '';
  errorMessage = '';
  successMessage = '';

  constructor(
    private authService: AuthService,
    private router: Router,
    private http: HttpClient,
    private cdr: ChangeDetectorRef
  ) {}

  onSubmit() {
    this.errorMessage = '';
    this.successMessage = '';

    if (!this.oldPassword) {
      this.errorMessage = 'Current password is required.';
      return;
    }
    if (!this.newPassword) {
      this.errorMessage = 'New password is required.';
      return;
    }
    if (this.newPassword.length < 6) {
      this.errorMessage = 'New password must be at least 6 characters long.';
      return;
    }
    if (this.newPassword !== this.confirmPassword) {
      this.errorMessage = 'New password and confirmation do not match.';
      return;
    }

    const payload = {
      oldPassword: this.oldPassword,
      newPassword: this.newPassword
    };

    this.http.patch('/api/user/change-password', payload, { responseType: 'text' }).subscribe({
      next: () => {
        // Clear redirect conditions on success
        sessionStorage.setItem('auth_must_change_password', 'false');
        if (this.authService.currentUser) {
          this.authService.currentUser.mustChangePassword = false;
        }
        
        this.successMessage = 'Password updated successfully! Redirecting...';
        this.cdr.detectChanges();
        
        setTimeout(() => {
          const role = this.authService.getRole();
          if (role === 'ADMIN') {
            this.router.navigate(['/dashboard']);
          } else {
            this.router.navigate(['/profile']);
          }
        }, 1500);
      },
      error: (err) => {
        console.error('Password change failed:', err);
        this.errorMessage = err.error || 'Failed to change password. Please verify current credentials.';
        this.cdr.detectChanges();
      }
    });
  }

  logout() {
    this.authService.logout();
    this.router.navigate(['/login']);
  }
}
