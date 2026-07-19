import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { Router } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AuthService } from '../auth.service';

@Component({
  selector: 'app-complete-profile',
  imports: [CommonModule, FormsModule],
  templateUrl: './complete-profile.html',
  styleUrl: './complete-profile.css'
})
export class CompleteProfile implements OnInit {
  role = '';
  loading = false;
  formError = '';

  // Student Form properties
  formRegistrationNumber = '';
  formFirstName = '';
  formLastName = '';
  formPhone = '';
  formDepartment = '';
  formProgram = '';

  // Professor Form properties
  formFirstNameProf = '';
  formLastNameProf = '';
  formEmployeeCode = '';
  formDepartmentProf = '';
  formDesignation = '';
  formContactNumberProf = '';

  // Admin Form properties
  formEmployeeCodeAdmin = '';
  formDepartmentAdmin = '';
  formDesignationAdmin = '';
  formFirstNameAdmin = '';
  formLastNameAdmin = '';
  formContactNumberAdmin = '';

  constructor(
    private authService: AuthService,
    private http: HttpClient,
    private router: Router,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit() {
    this.role = this.authService.getRole() || '';
    if (!this.role) {
      this.router.navigate(['/login']);
    }
  }

  saveStudentProfile() {
    this.formError = '';

    if (!this.formRegistrationNumber.trim()) {
      this.formError = 'Registration Number is required.';
      return;
    }
    if (!this.formFirstName.trim()) {
      this.formError = 'First Name is required.';
      return;
    }
    if (!this.formPhone.trim()) {
      this.formError = 'Phone Number is required.';
      return;
    }
    if (!this.formDepartment.trim()) {
      this.formError = 'Department is required.';
      return;
    }
    if (!this.formProgram.trim()) {
      this.formError = 'Program is required.';
      return;
    }

    this.loading = true;
    const payload = {
      registrationNumber: this.formRegistrationNumber.trim(),
      firstName: this.formFirstName.trim(),
      lastName: this.formLastName.trim(),
      phone: this.formPhone.trim(),
      department: this.formDepartment.trim(),
      program: this.formProgram.trim(),
      userId: this.authService.currentUser?.userId
    };

    this.http.post('/api/student', payload).subscribe({
      next: () => {
        sessionStorage.setItem('auth_profile_complete', 'true');
        this.loading = false;
        this.router.navigate(['/profile']);
      },
      error: (err) => {
        console.error('Failed to create student profile:', err);
        this.formError = err.error?.message || 'RegistrationNumber already exist';
        this.loading = false;
        this.cdr.detectChanges();
      }
    });
  }

  saveProfessorProfile() {
    this.formError = '';

    if (!this.formFirstNameProf.trim()) {
      this.formError = 'First Name is required.';
      return;
    }
    if (!this.formEmployeeCode.trim()) {
      this.formError = 'Employee Code is required.';
      return;
    }
    if (!this.formDepartmentProf.trim()) {
      this.formError = 'Department is required.';
      return;
    }
    if (!this.formDesignation.trim()) {
      this.formError = 'Designation is required.';
      return;
    }

    if (!this.formContactNumberProf.trim()) {
      this.formError = 'Contact Number is required.';
      return;
    }

    this.loading = true;
    const fullName = (this.formFirstNameProf.trim() + ' ' + this.formLastNameProf.trim()).trim();
    const payload = {
      name: fullName,
      employeeCode: this.formEmployeeCode.trim(),
      department: this.formDepartmentProf.trim(),
      designation: this.formDesignation.trim(),
      contactNumber: this.formContactNumberProf.trim(),
      userId: this.authService.currentUser?.userId
    };

    this.http.post('/api/professor', payload).subscribe({
      next: () => {
        sessionStorage.setItem('auth_profile_complete', 'true');
        this.loading = false;
        this.router.navigate(['/profile']);
      },
      error: (err) => {
        console.error('Failed to create professor profile:', err);
        this.formError = err.error?.message || "EmployeeCode already exists";
        this.loading = false;
        this.cdr.detectChanges();
      }
    });
  }

  saveAdminProfile() {
    this.formError = '';

    if (!this.formEmployeeCodeAdmin.trim()) {
      this.formError = 'Employee Code is required.';
      return;
    }
    if (!this.formDepartmentAdmin.trim()) {
      this.formError = 'Department is required.';
      return;
    }
    if (!this.formDesignationAdmin.trim()) {
      this.formError = 'Designation is required.';
      return;
    }

    if (!this.formFirstNameAdmin.trim()) {
      this.formError = 'First Name is required.';
      return;
    }
    if (!this.formLastNameAdmin.trim()) {
      this.formError = 'Last Name is required.';
      return;
    }
    if (!this.formContactNumberAdmin.trim()) {
      this.formError = 'Contact Number is required.';
      return;
    }

    this.loading = true;
    const payload = {
      employeeCode: this.formEmployeeCodeAdmin.trim(),
      department: this.formDepartmentAdmin.trim(),
      designation: this.formDesignationAdmin.trim(),
      firstName: this.formFirstNameAdmin.trim(),
      lastName: this.formLastNameAdmin.trim(),
      contactNumber: this.formContactNumberAdmin.trim(),
      userId: this.authService.currentUser?.userId
    };

    this.http.post('/api/admin-staff', payload).subscribe({
      next: () => {
        sessionStorage.setItem('auth_profile_complete', 'true');
        this.loading = false;
        this.router.navigate(['/profile']);
      },
      error: (err) => {
        console.error('Failed to create admin profile:', err);
        this.formError = err.error?.message || "EmployeeCode already exists";
        this.loading = false;
        this.cdr.detectChanges();
      }
    });
  }

  logout() {
    this.authService.logout();
    this.router.navigate(['/login']);
  }
}
