import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-professors',
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './professors.html',
  styleUrl: './professors.css',
})
export class Professors implements OnInit {
  professors: any[] = [];
  filteredProfessors: any[] = [];
  loading = false;
  error = '';

  // Filters
  searchQuery = '';
  selectedDepartment = '';
  departments: string[] = [];

  // Modal Control
  showModal = false;
  modalTitle = '';
  isEditMode = false;
  formError = '';

  // Form Properties
  currentProfessorId: number | null = null;
  formName = '';
  formEmployeeCode = '';
  formDepartment = '';
  formDesignation = '';
  formContactNumber = '';
  formUserId: number | null = null;

  // Detail Modal Properties
  showDetailModal = false;
  selectedProfessor: any = null;
  selectedProfessorCourses: any[] = [];
  coursesLoading = false;
  coursesError = '';

  constructor(private http: HttpClient, private cdr: ChangeDetectorRef) {}

  ngOnInit() {
    this.loadData();
  }

  loadData() {
    this.loading = true;
    this.error = '';

    let url = '/api/professor';
    if (this.searchQuery.trim()) {
      url += `?query=${encodeURIComponent(this.searchQuery.trim())}`;
    }

    this.http.get<any[]>(url).subscribe({
      next: (professorsList) => {
        this.professors = professorsList;
        this.extractDepartments();
        this.applyLocalFilters();
        this.loading = false;
        this.cdr.detectChanges();
      },
      error: (err) => {
        console.error('Failed to load professors:', err);
        this.error = 'Failed to load professor list.';
        this.loading = false;
        this.cdr.detectChanges();
      }
    });
  }

  extractDepartments() {
    const deps = this.professors
      .map((p) => p.department)
      .filter((d) => !!d);
    this.departments = Array.from(new Set(deps));
  }

  applyLocalFilters() {
    let result = [...this.professors];
    if (this.selectedDepartment) {
      result = result.filter((p) => p.department === this.selectedDepartment);
    }
    this.filteredProfessors = result;
  }

  applyFilters() {
    this.loadData();
  }

  onSearch() {
    this.loadData();
  }

  openEditModal(prof: any) {
    this.isEditMode = true;
    this.modalTitle = `Edit Professor: ${prof.name}`;
    this.currentProfessorId = prof.professorId;
    this.formName = prof.name;
    this.formEmployeeCode = prof.employeeCode;
    this.formDepartment = prof.department;
    this.formDesignation = prof.designation;
    this.formContactNumber = prof.contactNumber || '';
    this.formUserId = prof.userId;
    this.formError = '';
    this.showModal = true;
    this.cdr.detectChanges();
  }

  closeModal() {
    this.showModal = false;
    this.formError = '';
  }

  saveProfessor() {
    this.formError = '';

    if (!this.formName.trim()) {
      this.formError = 'Full Name is required.';
      return;
    }
    if (!this.formEmployeeCode.trim()) {
      this.formError = 'Employee Code is required.';
      return;
    }
    if (!this.formDepartment.trim()) {
      this.formError = 'Department is required.';
      return;
    }
    if (!this.formDesignation.trim()) {
      this.formError = 'Designation is required.';
      return;
    }
    if (!this.formContactNumber.trim()) {
      this.formError = 'Contact Number is required.';
      return;
    }

    const payload = {
      name: this.formName.trim(),
      employeeCode: this.formEmployeeCode.trim(),
      department: this.formDepartment.trim(),
      designation: this.formDesignation.trim(),
      contactNumber: this.formContactNumber.trim(),
      userId: this.formUserId,
    };

    this.http.put(`/api/professor/${this.currentProfessorId}/update-professor`, payload).subscribe({
      next: () => {
        this.closeModal();
        this.loadData();
      },
      error: (err) => {
        console.error('Update failed:', err);
        this.formError = err.error?.message || 'Failed to update professor profile.';
        this.cdr.detectChanges();
      }
    });
  }

  deleteProfessor(professorId: number) {
    if (confirm('Are you sure you want to delete this professor profile? This will not delete their User account.')) {
      this.http.delete(`/api/professor/${professorId}/delete-professor`, { responseType: 'text' }).subscribe({
        next: () => {
          this.loadData();
        },
        error: (err) => {
          console.error('Delete failed:', err);
          this.error = (typeof err.error === 'string') ? err.error : (err.error?.message || 'Failed to delete professor profile. Make sure they have no assigned courses.');
          this.cdr.detectChanges();
        }
      });
    }
  }

  viewDetails(prof: any) {
    this.selectedProfessor = prof;
    this.selectedProfessorCourses = [];
    this.coursesLoading = true;
    this.coursesError = '';
    this.showDetailModal = true;
    this.cdr.detectChanges();

    this.http.get<any[]>(`/api/course/${prof.professorId}/course-listbyprofessor`).subscribe({
      next: (courses) => {
        this.selectedProfessorCourses = courses;
        this.coursesLoading = false;
        this.cdr.detectChanges();
      },
      error: (err) => {
        console.error('Failed to load courses:', err);
        this.coursesError = 'Failed to load assigned courses.';
        this.coursesLoading = false;
        this.cdr.detectChanges();
      }
    });
  }

  closeDetailModal() {
    this.showDetailModal = false;
    this.selectedProfessor = null;
    this.selectedProfessorCourses = [];
  }
}
