import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterLink, Router } from '@angular/router';
import { AuthService } from '../auth.service';
import { AcademicPeriodService } from '../academic-period.service';

@Component({
  selector: 'app-profile',
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './profile.html',
  styleUrl: './profile.css'
})
export class MyProfile implements OnInit {
  user: any = null;
  selectedPeriodId: number | null = null;
  student: any = null;
  loading = false;
  saving = false;
  error = '';
  success = '';

  // Edit Mode state
  editMode = false;

  // Form properties
  formPhone = '';
  formFirstName = '';
  formLastName = '';
  formRegNum = '';
  formDepartment = '';
  formProgram = '';

  // Backup values for edit cancel
  tempPhone = '';
  tempFirstName = '';
  tempLastName = '';
  tempRegNum = '';
  tempDepartment = '';
  tempProgram = '';

  // Academic stats from dashboard
  stats: any = null;

  constructor(
    private http: HttpClient,
    private authService: AuthService,
    private cdr: ChangeDetectorRef,
    private router: Router,
    private academicPeriodService: AcademicPeriodService
  ) {}

  ngOnInit() {
    const role = this.authService.getRole();
    if (role === 'ADMIN') {
      this.router.navigate(['/dashboard']);
      return;
    }
    
    this.academicPeriodService.selectedPeriod$.subscribe(period => {
      if (period) {
        this.selectedPeriodId = period.academicPeriodId;
        this.loadProfile();
      }
    });
  }

  loadProfile() {
    this.loading = true;
    this.error = '';
    this.success = '';
    this.cdr.detectChanges();

    this.http.get<any>('/api/user/me').subscribe({
      next: (userData) => {
        this.user = userData;
        
        if (userData.role === 'PROFESSOR') {
          this.http.get<any>('/api/professor/professorprofile').subscribe({
            next: (profData) => {
              this.student = profData; // Reuse student reference container for profiles
              this.formPhone = profData.contactNumber || '';
              this.formFirstName = profData.name.split(' ')[0] || '';
              this.formLastName = profData.name.split(' ').slice(1).join(' ') || '';
              this.formRegNum = profData.employeeCode || '';
              this.formDepartment = profData.department || '';
              this.formProgram = profData.designation || '';
              
              // Load professor stats dynamically from dashboard endpoint
              let profDashUrl = `/api/dashboard/${userData.userId}/user-details`;
              if (this.selectedPeriodId) {
                profDashUrl += `?academicPeriodId=${this.selectedPeriodId}`;
              }
              this.http.get<any>(profDashUrl).subscribe({
                next: (dashData) => {
                  const statistics = dashData.statistics || {};
                  this.stats = {
                    totalCourses: statistics.totalCourses || 0,
                    totalAssignments: statistics.totalAssignments || 0,
                    pendingAssignments: statistics.awaitingGrading || 0,
                    gradedAssignments: statistics.gradedAssignments || 0
                  };
                  this.loading = false;
                  this.cdr.detectChanges();
                },
                error: () => {
                  this.stats = {
                    totalCourses: 0,
                    totalAssignments: 0,
                    pendingAssignments: 0,
                    gradedAssignments: 0
                  };
                  this.loading = false;
                  this.cdr.detectChanges();
                }
              });
            },
            error: (err) => {
              console.error('Failed to load professor details:', err);
              this.error = 'Professor profile details not found.';
              this.loading = false;
              this.cdr.detectChanges();
            }
          });
        } else {
          this.http.get<any>('/api/student/student-profile').subscribe({
            next: (studentData) => {
              this.student = studentData;
              this.formPhone = studentData.phone || '';
              this.formFirstName = studentData.firstName || '';
              this.formLastName = studentData.lastName || '';
              this.formRegNum = studentData.registrationNumber || '';
              this.formDepartment = studentData.department || '';
              this.formProgram = studentData.program || '';
              
              // Load dashboard stats for academic cards
              let studDashUrl = `/api/dashboard/${userData.userId}/user-details`;
              if (this.selectedPeriodId) {
                studDashUrl += `?academicPeriodId=${this.selectedPeriodId}`;
              }
              this.http.get<any>(studDashUrl).subscribe({
                next: (dashData) => {
                  this.stats = dashData.statistics;
                  if (dashData.studentProfile) {
                    this.student = {
                      ...this.student,
                      cgpa: dashData.studentProfile.cgpa,
                      totalCreditsCompleted: dashData.studentProfile.totalCreditsCompleted,
                      totalCreditsEarned: dashData.studentProfile.totalCreditsEarned,
                      currentHalfGpa: dashData.studentProfile.currentHalfGpa
                    };
                  }
                  this.loading = false;
                  this.cdr.detectChanges();
                },
                error: () => {
                  this.loading = false;
                  this.cdr.detectChanges();
                }
              });
            },
            error: (err) => {
              console.error('Failed to load student profile details:', err);
              this.error = 'Student academic profile details not found.';
              this.loading = false;
              this.cdr.detectChanges();
            }
          });
        }
      },
      error: (err) => {
        console.error('Failed to load user account:', err);
        this.error = 'User account credentials not found.';
        this.loading = false;
        this.cdr.detectChanges();
      }
    });
  }

  toggleEditMode() {
    if (this.editMode) {
      this.cancelEdit();
    } else {
      this.enterEditMode();
    }
  }

  enterEditMode() {
    this.tempPhone = this.formPhone;
    this.tempFirstName = this.formFirstName;
    this.tempLastName = this.formLastName;
    this.tempRegNum = this.formRegNum;
    this.tempDepartment = this.formDepartment;
    this.tempProgram = this.formProgram;
    this.editMode = true;
    this.cdr.detectChanges();
  }

  cancelEdit() {
    this.formPhone = this.tempPhone;
    this.formFirstName = this.tempFirstName;
    this.formLastName = this.tempLastName;
    this.formRegNum = this.tempRegNum;
    this.formDepartment = this.tempDepartment;
    this.formProgram = this.tempProgram;
    this.editMode = false;
    this.cdr.detectChanges();
  }

  saveProfile() {
    if (!this.student) return;

    this.saving = true;
    this.error = '';
    this.success = '';
    this.cdr.detectChanges();

    if (this.user.role === 'PROFESSOR') {
      const payload = {
        name: (this.formFirstName.trim() + ' ' + this.formLastName.trim()).trim(),
        employeeCode: this.formRegNum.trim(),
        department: this.formDepartment.trim(),
        designation: this.formProgram.trim(),
        contactNumber: this.formPhone.trim(),
        userId: this.user.userId
      };

      this.http.put<any>(`/api/professor/${this.student.professorId}/update-professor`, payload).subscribe({
        next: (updatedProf) => {
          this.student = updatedProf;
          this.formPhone = updatedProf.contactNumber || '';
          this.formFirstName = updatedProf.name.split(' ')[0] || '';
          this.formLastName = updatedProf.name.split(' ').slice(1).join(' ') || '';
          this.formRegNum = updatedProf.employeeCode || '';
          this.formDepartment = updatedProf.department || '';
          this.formProgram = updatedProf.designation || '';
          this.success = 'Profile details updated successfully!';
          this.editMode = false;
          this.saving = false;
          this.cdr.detectChanges();
        },
        error: (err) => {
          console.error('Failed to update professor details:', err);
          if (Array.isArray(err.error)) {
            this.error = err.error.join(', ');
          } else if (typeof err.error === 'string') {
            this.error = err.error;
          } else {
            this.error = err.error?.message || 'Failed to save updated profile details.';
          }
          this.saving = false;
          this.cdr.detectChanges();
        }
      });
    } else {
      const payload = {
        registrationNumber: this.formRegNum.trim(),
        firstName: this.formFirstName.trim(),
        lastName: this.formLastName.trim(),
        phone: this.formPhone.trim(),
        department: this.formDepartment.trim(),
        program: this.formProgram.trim(),
        userId: this.user.userId
      };

      this.http.put<any>(`/api/student/${this.student.studentId}/update-std`, payload).subscribe({
        next: (updatedStudent) => {
          this.student = updatedStudent;
          this.formPhone = updatedStudent.phone || '';
          this.formFirstName = updatedStudent.firstName || '';
          this.formLastName = updatedStudent.lastName || '';
          this.formRegNum = updatedStudent.registrationNumber || '';
          this.formDepartment = updatedStudent.department || '';
          this.formProgram = updatedStudent.program || '';
          this.success = 'Profile details updated successfully!';
          this.editMode = false;
          this.saving = false;
          this.cdr.detectChanges();
        },
        error: (err) => {
          console.error('Failed to update student details:', err);
          if (Array.isArray(err.error)) {
            this.error = err.error.join(', ');
          } else if (typeof err.error === 'string') {
            this.error = err.error;
          } else {
            this.error = err.error?.message || 'RegistrationNumber already exist.';
          }
          this.saving = false;
          this.cdr.detectChanges();
        }
      });
    }
  }
}
