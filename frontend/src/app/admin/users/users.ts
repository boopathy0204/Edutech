import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { AuthService } from '../../auth.service';

@Component({
  selector: 'app-users',
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './users.html',
  styleUrl: './users.css',
})
export class Users implements OnInit {
  users: any[] = [];
  filteredUsers: any[] = [];
  loading = false;
  error = '';
  loggedUserId: number | null = null;

  // Filter properties
  searchQuery = '';
  selectedRole = '';

  // Modal control
  showModal = false;
  modalTitle = '';
  isEditMode = false;
  formError = '';

  // Form values
  currentUserId: number | null = null;
  formUsername = '';
  formEmail = '';
  formPassword = '';
  formRole = '';

  // Temp Password display
  showTempPasswordBanner = false;
  tempPasswordValue = '';
  tempPasswordUser = '';

  // Importer properties
  showImportCard = false;
  selectedFile: File | null = null;
  importLoading = false;
  importError = '';
  importSummary: any = null;

  constructor(
    private http: HttpClient, 
    private cdr: ChangeDetectorRef,
    private authService: AuthService
  ) {}

  ngOnInit() {
    this.loggedUserId = this.authService.currentUser?.userId;
    this.loadUsers();
  }

  loadUsers() {
    this.loading = true;
    this.error = '';

    let url = '/api/user';
    const params: string[] = [];
    if (this.searchQuery.trim()) {
      params.push(`query=${encodeURIComponent(this.searchQuery.trim())}`);
    }
    if (this.selectedRole) {
      params.push(`role=${this.selectedRole}`);
    }
    if (params.length > 0) {
      url += `?${params.join('&')}`;
    }

    this.http.get<any[]>(url).subscribe({
      next: (data) => {
        this.users = data;
        this.filteredUsers = data;
        this.loading = false;
        this.cdr.detectChanges();
      },
      error: (err) => {
        console.error('Failed to load users:', err);
        this.error = 'Failed to load user list from server.';
        this.loading = false;
        this.cdr.detectChanges();
      }
    });
  }

  applyFilters() {
    this.loadUsers();
  }

  onSearch() {
    this.loadUsers();
  }

  openCreateModal() {
    this.isEditMode = false;
    this.modalTitle = 'Create New User Account';
    this.currentUserId = null;
    this.formUsername = '';
    this.formEmail = '';
    this.formPassword = '';
    this.formRole = '';
    this.formError = '';
    this.showModal = true;
  }

  openEditModal(user: any) {
    this.isEditMode = true;
    this.modalTitle = `Edit User: ${user.username}`;
    this.currentUserId = user.userId;
    this.formUsername = user.username;
    this.formEmail = user.email;
    this.formPassword = '';
    this.formRole = user.role;
    this.formError = '';
    this.showModal = true;
  }

  closeModal() {
    this.showModal = false;
    this.formError = '';
  }

  saveUser() {
    this.formError = '';

    // Validate fields
    if (!this.formEmail.trim()) {
      this.formError = 'Email address is required.';
      return;
    }
    if (!this.formRole) {
      this.formError = 'Please select a role.';
      return;
    }

    const payload: any = {
      email: this.formEmail.trim(),
      role: this.formRole,
    };

    if (this.formUsername.trim()) {
      payload.username = this.formUsername.trim();
    }

    if (this.isEditMode) {
      // Edit User
      this.http.put(`/api/user/${this.currentUserId}/update-user`, payload, { responseType: 'text' }).subscribe({
        next: () => {
          this.closeModal();
          this.loadUsers();
        },
        error: (err) => {
          console.error('Update failed:', err);
          this.formError = err.error?.message || 'Failed to update user account.';
          this.cdr.detectChanges();
        }
      });
    } else {
      // Create User
      if (this.formPassword.trim()) {
        payload.password = this.formPassword.trim();
      }

      this.http.post<any>('/api/user', payload).subscribe({
        next: (res) => {
          this.closeModal();
          this.loadUsers();

          // If a temporary password was generated, display it to the admin
          if (res.temporaryPassword) {
            this.tempPasswordUser = res.username;
            this.tempPasswordValue = res.temporaryPassword;
            this.showTempPasswordBanner = true;
          }
          this.cdr.detectChanges();
        },
        error: (err) => {
          console.error('Creation failed:', err);
          this.formError = err.error?.message || err.error;
          this.cdr.detectChanges();
        }
      });
    }
  }

  deleteUser(userId: number) {
    if (this.loggedUserId && userId === this.loggedUserId) {
      alert("You cannot delete your own admin user account.");
      return;
    }
    if (confirm('Are you sure you want to delete this user? This action cannot be undone.')) {
      this.http.delete(`/api/user/${userId}/delete-delete`, { responseType: 'text' }).subscribe({
        next: () => {
          this.loadUsers();
        },
        error: (err) => {
          console.error('Delete failed:', err);
          this.error = (typeof err.error === 'string') ? err.error : (err.error?.message || 'Failed to delete user account.');
          this.cdr.detectChanges();
        }
      });
    }
  }

  dismissTempBanner() {
    this.showTempPasswordBanner = false;
    this.tempPasswordValue = '';
    this.tempPasswordUser = '';
  }

  // Bulk Importer Methods
  openImportCard() {
    this.showImportCard = true;
    this.selectedFile = null;
    this.importError = '';
    this.importSummary = null;
  }

  closeImport() {
    this.showImportCard = false;
    this.selectedFile = null;
    this.importError = '';
    this.importSummary = null;
  }

  onFileSelected(event: any) {
    const file = event.target.files[0];
    if (file) {
      this.selectedFile = file;
    }
  }

  uploadFile() {
    if (!this.selectedFile) {
      this.importError = 'Please select a CSV or Excel file first.';
      this.cdr.detectChanges();
      return;
    }

    this.importLoading = true;
    this.importError = '';
    this.importSummary = null;
    this.cdr.detectChanges();

    const formData = new FormData();
    formData.append('file', this.selectedFile);

    this.http.post<any>('/api/user/bulk-import', formData).subscribe({
      next: (res) => {
        this.importSummary = res;
        this.importLoading = false;
        this.loadUsers();
        this.cdr.detectChanges();
      },
      error: (err) => {
        console.error('User bulk import failed:', err);
        this.importError = err.error?.message || 'Failed to process bulk import. Please check file structure.';
        this.importLoading = false;
        this.cdr.detectChanges();
      }
    });
  }

  exportCredentials() {
    if (!this.importSummary || !this.importSummary.results) return;

    // Filter results to download successful imports only
    const successes = this.importSummary.results.filter((r: any) => r.success);
    if (successes.length === 0) return;

    let csvContent = 'data:text/csv;charset=utf-8,';
    csvContent += 'Name,Email,Username,Temporary Password,Role\n';

    successes.forEach((r: any) => {
      const name = r.name ? `"${r.name.replace(/"/g, '""')}"` : '';
      const email = r.email ? `"${r.email.replace(/"/g, '""')}"` : '';
      const username = r.generatedUsername ? `"${r.generatedUsername.replace(/"/g, '""')}"` : '';
      const password = r.generatedPassword ? `"${r.generatedPassword.replace(/"/g, '""')}"` : '';
      const role = r.role ? `"${r.role.replace(/"/g, '""')}"` : '';
      csvContent += `${name},${email},${username},${password},${role}\n`;
    });

    const encodedUri = encodeURI(csvContent);
    const link = document.createElement('a');
    link.setAttribute('href', encodedUri);
    link.setAttribute('download', 'new_user_credentials.csv');
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
  }
}
