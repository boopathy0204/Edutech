import { Component, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../auth.service';

@Component({
  selector: 'app-login',
  imports: [CommonModule, FormsModule],
  templateUrl: './login.html',
  styleUrl: './login.css',
})
export class Login {
  username = '';
  password = '';
  showPassword = false;
  errorMessage = '';
  successMessage = '';

  constructor(
    private authService: AuthService,
    private router: Router,
    private cdr: ChangeDetectorRef
  ) {}

  togglePasswordVisibility() {
    this.showPassword = !this.showPassword;
  }

  onSubmit() {
    this.errorMessage = '';
    this.successMessage = '';

    this.authService.login(this.username, this.password).subscribe({
      next: (response) => {
        console.log('Login successful, token stored:', this.authService.getToken());
        this.successMessage = 'Login successful!';
        this.cdr.detectChanges();
        if (response.mustChangePassword) {
          this.router.navigate(['/change-password']);
        } else {
          const role = this.authService.getRole();
          if (role === 'ADMIN') {
            this.router.navigate(['/dashboard']);
          } else {
            this.router.navigate(['/profile']);
          }
        }
      },
      error: (error) => {
        console.error('API Error:', error);
        let msg = 'Invalid username or password';
        const errPayload = error.error;
        const errMsgText = (typeof errPayload === 'string')
          ? errPayload
          : (errPayload?.message || error.message || '');

        const lowerMsg = errMsgText.toLowerCase();
        if (lowerMsg.includes('disabled') || lowerMsg.includes('active')) {
          msg = 'Your account has been disabled. Please contact the system administrator.';
        } else if (lowerMsg.includes('locked')) {
          msg = 'Your account has been locked. Please contact the system administrator.';
        }

        this.errorMessage = msg;
        this.cdr.detectChanges();
      }
    });
  }
}
