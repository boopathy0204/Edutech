import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { AuthService } from '../../auth.service';

@Component({
  selector: 'app-staff',
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './staff.html',
  styleUrl: './staff.css',
})
export class Staff implements OnInit {
  staffMembers: any[] = [];
  filteredStaff: any[] = [];
  loading = false;
  error = '';
  loggedUserId: number | null = null;

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
  currentAdminId: number | null = null;
  formEmployeeCode = '';
  formDepartment = '';
  formDesignation = '';
  formFirstName = '';
  formLastName = '';
  formContactNumber = '';
  formUserId: number | null = null;

  constructor(
    private http: HttpClient, 
    private cdr: ChangeDetectorRef,
    private authService: AuthService
  ) {}

  ngOnInit() {
    this.loggedUserId = this.authService.currentUser?.userId;
    this.loadData();
  }

  loadData() {
    this.loading = true;
    this.error = '';

    let url = '/api/admin-staff';
    if (this.searchQuery.trim()) {
      url += `?query=${encodeURIComponent(this.searchQuery.trim())}`;
    }

    this.http.get<any[]>(url).subscribe({
      next: (staffList) => {
        this.staffMembers = staffList;
        this.extractDepartments();
        this.applyLocalFilters();
        this.loading = false;
        this.cdr.detectChanges();
      },
      error: (err) => {
        console.error('Failed to load admin staff:', err);
        this.error = 'Failed to load administrative staff list.';
        this.loading = false;
        this.cdr.detectChanges();
      }
    });
  }

  extractDepartments() {
    const deps = this.staffMembers
      .map((s) => s.department)
      .filter((d) => !!d);
    this.departments = Array.from(new Set(deps));
  }

  applyLocalFilters() {
    let result = [...this.staffMembers];
    if (this.selectedDepartment) {
      result = result.filter((s) => s.department === this.selectedDepartment);
    }
    this.filteredStaff = result;
  }

  applyFilters() {
    this.loadData();
  }

  onSearch() {
    this.loadData();
  }

  openEditModal(member: any) {
    this.isEditMode = true;
    this.modalTitle = `Edit Staff: @${member.username}`;
    this.currentAdminId = member.adminId;
    this.formEmployeeCode = member.employeeCode;
    this.formDepartment = member.department;
    this.formDesignation = member.designation;
    this.formFirstName = member.firstName || '';
    this.formLastName = member.lastName || '';
    this.formContactNumber = member.contactNumber || '';
    this.formUserId = member.userId;
    this.formError = '';
    this.showModal = true;
    this.cdr.detectChanges();
  }

  closeModal() {
    this.showModal = false;
    this.formError = '';
  }

  saveStaff() {
    this.formError = '';

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
    if (!this.formFirstName.trim()) {
      this.formError = 'First Name is required.';
      return;
    }
    if (!this.formLastName.trim()) {
      this.formError = 'Last Name is required.';
      return;
    }
    if (!this.formContactNumber.trim()) {
      this.formError = 'Contact Number is required.';
      return;
    }

    const payload = {
      employeeCode: this.formEmployeeCode.trim(),
      department: this.formDepartment.trim(),
      designation: this.formDesignation.trim(),
      firstName: this.formFirstName.trim(),
      lastName: this.formLastName.trim(),
      contactNumber: this.formContactNumber.trim(),
      userId: this.formUserId,
    };

    this.http.put(`/api/admin-staff/${this.currentAdminId}/update`, payload).subscribe({
      next: () => {
        this.closeModal();
        this.loadData();
      },
      error: (err) => {
        console.error('Update failed:', err);
        this.formError = err.error?.message || 'Failed to update admin staff profile.';
        this.cdr.detectChanges();
      }
    });
  }

  deleteStaff(adminId: number) {
    const staff = this.staffMembers.find(s => s.adminId === adminId);
    if (staff && staff.userId === this.loggedUserId) {
      alert("You cannot delete your own admin staff profile.");
      return;
    }
    if (confirm('Are you sure you want to delete this admin staff profile? This will not delete their User account.')) {
      this.http.delete(`/api/admin-staff/${adminId}/delete-admin`, { responseType: 'text' }).subscribe({
        next: () => {
          this.loadData();
        },
        error: (err) => {
          console.error('Delete failed:', err);
          this.error = (typeof err.error === 'string') ? err.error : (err.error?.message || 'Failed to delete administrative staff profile.');
          this.cdr.detectChanges();
        }
      });
    }
  }
}
