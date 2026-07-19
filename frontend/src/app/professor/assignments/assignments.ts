import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { forkJoin, of } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { AuthService } from '../../auth.service';
import { AcademicPeriodService } from '../../academic-period.service';

@Component({
  selector: 'app-professor-assignments',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './assignments.html',
  styleUrl: './assignments.css'
})
export class ProfessorAssignments implements OnInit {
  assignments: any[] = [];
  courses: any[] = [];
  filteredAssignments: any[] = [];
  loading = false;
  saving = false;
  error = '';
  success = '';

  // Filter and search variables
  searchQuery = '';
  courseFilter = '';
  currentPage = 1;
  pageSize = 10;
  totalPages = 1;
  professorId: number | null = null;
  selectedPeriodId: number | null = null;

  // Modal dialog variables
  showModal = false;
  modalTitle = 'Create Assignment';
  editingAssignmentId: number | null = null;
  
  // Form fields
  formCourseId: number | null = null;
  formTitle = '';
  formDesc = '';
  formDueDate = '';
  formMaxMarks = 100;

  constructor(
    private http: HttpClient,
    private cdr: ChangeDetectorRef,
    private authService: AuthService,
    private academicPeriodService: AcademicPeriodService
  ) {}

  ngOnInit() {
    this.academicPeriodService.selectedPeriod$.subscribe(period => {
      if (period) {
        this.selectedPeriodId = period.academicPeriodId;
        this.loadCourses();
      }
    });
  }

  loadCourses() {
    this.loading = true;
    this.cdr.detectChanges();

    this.authService.getProfessorProfile().subscribe({
      next: (profile) => {
        this.professorId = profile.professorId;
        let url = `/api/course/${profile.professorId}/course-listbyprofessor`;
        if (this.selectedPeriodId) {
          url += `?academicPeriodId=${this.selectedPeriodId}`;
        }
        this.http.get<any[]>(url).subscribe({
          next: (courseData) => {
            this.courses = courseData;
            if (this.courses.length > 0) {
              this.formCourseId = this.courses[0].courseId;
            } else {
              this.formCourseId = null;
            }
            this.loadAssignments();
          },
          error: (err) => {
            console.error('Failed to load courses:', err);
            this.loading = false;
            this.cdr.detectChanges();
          }
        });
      },
      error: (err) => {
        console.error('Failed to load professor profile:', err);
        this.error = 'Failed to load professor details.';
        this.loading = false;
        this.cdr.detectChanges();
      }
    });
  }

  loadAssignments() {
    if (!this.professorId) return;

    this.loading = true;
    this.error = '';
    this.cdr.detectChanges();

    let url = `/api/assignment/${this.professorId}/professor-assignment`;
    const params: string[] = [];
    if (this.courseFilter) {
      params.push(`courseId=${this.courseFilter}`);
    }
    if (this.searchQuery.trim()) {
      params.push(`query=${encodeURIComponent(this.searchQuery.trim())}`);
    }
    if (this.selectedPeriodId) {
      params.push(`academicPeriodId=${this.selectedPeriodId}`);
    }
    if (params.length > 0) {
      url += `?${params.join('&')}`;
    }

    this.http.get<any[]>(url).subscribe({
      next: (data) => {
        this.assignments = data.map(a => ({
          ...a,
          status: new Date(a.dueDate) > new Date() ? 'Active' : 'Expired'
        }));
        this.applyFilters();
        this.loading = false;
        this.cdr.detectChanges();
      },
      error: (err) => {
        console.error('Failed to resolve assignments list:', err);
        this.assignments = [];
        this.applyFilters();
        this.loading = false;
        this.cdr.detectChanges();
      }
    });
  }

  applyFilters() {
    const result = [...this.assignments];
    this.totalPages = Math.ceil(result.length / this.pageSize) || 1;
    const startIndex = (this.currentPage - 1) * this.pageSize;
    this.filteredAssignments = result.slice(startIndex, startIndex + this.pageSize);
    this.cdr.detectChanges();
  }

  applySearch() {
    this.currentPage = 1;
    this.loadAssignments();
  }

  onSearch() {
    this.currentPage = 1;
    this.loadAssignments();
  }

  changePage(page: number) {
    if (page >= 1 && page <= this.totalPages) {
      this.currentPage = page;
      this.applyFilters();
    }
  }

  openCreateModal() {
    this.modalTitle = 'Create Assignment';
    this.editingAssignmentId = null;
    this.formCourseId = this.courses.length > 0 ? this.courses[0].courseId : null;
    this.formTitle = '';
    this.formDesc = '';
    this.formDueDate = '';
    this.formMaxMarks = 100;
    this.showModal = true;
    this.cdr.detectChanges();
  }

  openEditModal(assignment: any) {
    this.modalTitle = 'Edit Assignment';
    this.editingAssignmentId = assignment.assignmentId;
    this.formCourseId = assignment.courseId;
    this.formTitle = assignment.title;
    this.formDesc = assignment.description;
    this.formDueDate = assignment.dueDate ? assignment.dueDate.substring(0, 16) : '';
    this.formMaxMarks = assignment.maxMarks;
    this.showModal = true;
    this.cdr.detectChanges();
  }

  closeModal() {
    this.showModal = false;
    this.cdr.detectChanges();
  }

  saveAssignment() {
    if (!this.formTitle.trim() || !this.formCourseId || !this.formDueDate) {
      this.error = 'Please fill out all required fields.';
      return;
    }

    this.saving = true;
    this.error = '';
    this.success = '';
    this.cdr.detectChanges();

    const payload = {
      title: this.formTitle.trim(),
      description: this.formDesc.trim(),
      dueDate: new Date(this.formDueDate).toISOString(),
      maxMarks: Number(this.formMaxMarks),
      courseId: Number(this.formCourseId)
    };

    if (this.editingAssignmentId) {
      this.http.put<any>(`/api/assignment/${this.editingAssignmentId}/update-assignment`, payload).subscribe({
        next: () => {
          this.success = 'Assignment updated successfully!';
          this.loadAssignments();
          this.saving = false;
          this.showModal = false;
        },
        error: (err) => {
          console.error('Update failed:', err);
          this.error = (typeof err.error === 'string') ? err.error : (err.error?.message || 'Failed to update assignment in the database.');
          this.saving = false;
          this.cdr.detectChanges();
        }
      });
    } else {
      this.http.post<any>('/api/assignment', payload).subscribe({
        next: () => {
          this.success = 'Assignment created successfully!';
          this.loadAssignments();
          this.saving = false;
          this.showModal = false;
        },
        error: (err) => {
          console.error('Create failed:', err);
          this.error = (typeof err.error === 'string') ? err.error : (err.error?.message || 'Failed to create assignment in the database.');
          this.saving = false;
          this.cdr.detectChanges();
        }
      });
    }
  }

  deleteAssignment(assignmentId: number) {
    if (confirm('Are you sure you want to delete this assignment permanently?')) {
      this.http.delete(`/api/assignment/${assignmentId}/delete-assignment`, { responseType: 'text' }).subscribe({
        next: () => {
          this.success = 'Assignment deleted successfully!';
          this.loadAssignments();
        },
        error: (err) => {
          console.error('Delete failed:', err);
          this.error = 'Failed to delete assignment from the database.';
          this.cdr.detectChanges();
        }
      });
    }
  }
}
