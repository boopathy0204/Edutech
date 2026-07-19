import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-students',
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './students.html',
  styleUrl: './students.css',
})
export class Students implements OnInit {
  students: any[] = [];
  filteredStudents: any[] = [];
  loading = false;
  error = '';

  // Filters
  searchQuery = '';
  selectedDepartment = '';
  departments: string[] = [];

  // Modal control
  showModal = false;
  modalTitle = '';
  isEditMode = false;
  formError = '';

  // Form properties
  currentStudentId: number | null = null;
  formRegistrationNumber = '';
  formFirstName = '';
  formLastName = '';
  formPhone = '';
  formDepartment = '';
  formProgram = '';
  formUserId: number | null = null;

  // Detail Modal properties
  showDetailModal = false;
  selectedStudent: any = null;
  selectedStudentCourses: any[] = [];
  coursesLoading = false;
  coursesError = '';

  constructor(private http: HttpClient, private cdr: ChangeDetectorRef) {}

  ngOnInit() {
    this.loadData();
  }

  loadData() {
    this.loading = true;
    this.error = '';

    let url = '/api/student';
    if (this.searchQuery.trim()) {
      url += `?query=${encodeURIComponent(this.searchQuery.trim())}`;
    }

    this.http.get<any[]>(url).subscribe({
      next: (studentsList) => {
        this.students = studentsList;
        this.extractDepartments();
        this.applyLocalFilters();
        this.loading = false;
        this.cdr.detectChanges();
      },
      error: (err) => {
        console.error('Failed to load students:', err);
        this.error = 'Failed to load students list.';
        this.loading = false;
        this.cdr.detectChanges();
      }
    });
  }

  extractDepartments() {
    const deps = this.students
      .map((s) => s.department)
      .filter((d) => !!d);
    this.departments = Array.from(new Set(deps));
  }

  applyLocalFilters() {
    let result = [...this.students];
    if (this.selectedDepartment) {
      result = result.filter((s) => s.department === this.selectedDepartment);
    }
    this.filteredStudents = result;
  }

  applyFilters() {
    this.loadData();
  }

  onSearch() {
    this.loadData();
  }

  openEditModal(student: any) {
    this.isEditMode = true;
    this.modalTitle = `Edit Student: ${student.firstName} ${student.lastName}`;
    this.currentStudentId = student.studentId;
    this.formRegistrationNumber = student.registrationNumber;
    this.formFirstName = student.firstName;
    this.formLastName = student.lastName;
    this.formPhone = student.phone;
    this.formDepartment = student.department;
    this.formProgram = student.program;
    this.formUserId = student.userId;
    this.formError = '';
    this.showModal = true;
    this.cdr.detectChanges();
  }

  closeModal() {
    this.showModal = false;
    this.formError = '';
  }

  saveStudent() {
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
      this.formError = 'Program field is required.';
      return;
    }

    const payload = {
      registrationNumber: this.formRegistrationNumber.trim(),
      firstName: this.formFirstName.trim(),
      lastName: this.formLastName.trim(),
      phone: this.formPhone.trim(),
      department: this.formDepartment.trim(),
      program: this.formProgram.trim(),
      userId: this.formUserId,
    };

    this.http.put(`/api/student/${this.currentStudentId}/update-std`, payload).subscribe({
      next: () => {
        this.closeModal();
        this.loadData();
      },
      error: (err) => {
        console.error('Update failed:', err);
        this.formError = err.error?.message || 'Failed to update student profile.';
        this.cdr.detectChanges();
      }
    });
  }

  deleteStudent(studentId: number) {
    if (confirm('Are you sure you want to delete this student profile? This will not delete their User account.')) {
      this.http.delete(`/api/student/${studentId}/delete-std`, { responseType: 'text' }).subscribe({
        next: () => {
          this.loadData();
        },
        error: (err) => {
          console.error('Delete failed:', err);
          this.error = (typeof err.error === 'string') ? err.error : (err.error?.message || 'Failed to delete student profile. Make sure the student is not enrolled in any courses.');
          this.cdr.detectChanges();
        }
      });
    }
  }

  viewDetails(student: any) {
    this.selectedStudent = student;
    this.selectedStudentCourses = [];
    this.coursesLoading = true;
    this.coursesError = '';
    this.showDetailModal = true;
    this.cdr.detectChanges();

    this.http.get<any[]>(`/api/enrollment/${student.studentId}/course-listbystudent`).subscribe({
      next: (courses) => {
        this.selectedStudentCourses = courses;
        this.coursesLoading = false;
        this.cdr.detectChanges();
      },
      error: (err) => {
        console.error('Failed to load student courses:', err);
        this.coursesError = 'Failed to load enrolled courses.';
        this.coursesLoading = false;
        this.cdr.detectChanges();
      }
    });
  }

  closeDetailModal() {
    this.showDetailModal = false;
    this.selectedStudent = null;
    this.selectedStudentCourses = [];
  }
}
