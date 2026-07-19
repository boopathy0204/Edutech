import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

import { AuthService } from '../../auth.service';
import { AcademicPeriodService } from '../../academic-period.service';

@Component({
  selector: 'app-professor-materials',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './materials.html',
  styleUrl: './materials.css'
})
export class ProfessorMaterials implements OnInit {
  materials: any[] = [];
  courses: any[] = [];
  loading = false;
  uploading = false;
  error = '';
  success = '';
  professorId: number | null = null;
  selectedPeriodId: number | null = null;

  // Filter and search variables
  searchQuery = '';
  courseFilter = '';

  // Form fields
  selectedCourseId: number | null = null;
  materialTitle = '';
  selectedFile: File | null = null;
  showModal = false;

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
        this.loadInitialData();
      }
    });
  }

  loadInitialData() {
    this.loading = true;
    this.error = '';
    this.cdr.detectChanges();

    this.authService.getProfessorProfile().subscribe({
      next: (profile) => {
        this.professorId = profile.professorId;
        // Load courses
        let url = `/api/course/${profile.professorId}/course-listbyprofessor`;
        if (this.selectedPeriodId) {
          url += `?academicPeriodId=${this.selectedPeriodId}`;
        }
        this.http.get<any[]>(url).subscribe({
          next: (courseData) => {
            this.courses = courseData;
            if (this.courses.length > 0) {
              this.selectedCourseId = this.courses[0].courseId;
            } else {
              this.selectedCourseId = null;
            }
            this.loadAllMaterials();
          },
          error: (err) => {
            console.error('Failed to load courses:', err);
            this.error = 'Failed to load assigned courses.';
            this.loading = false;
            this.cdr.detectChanges();
          }
        });
      },
      error: (err) => {
        console.error('Failed to load professor profile:', err);
        this.error = 'Failed to resolve professor profile. Please login again.';
        this.loading = false;
        this.cdr.detectChanges();
      }
    });
  }

  loadAllMaterials() {
    if (!this.professorId) return;

    this.loading = true;
    this.cdr.detectChanges();

    let url = `/api/course-material/professor/${this.professorId}/professor-materials`;
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
        this.materials = data;
        this.loading = false;
        this.cdr.detectChanges();
      },
      error: (err) => {
        console.error('Failed to query materials:', err);
        this.materials = [];
        this.loading = false;
        this.cdr.detectChanges();
      }
    });
  }

  applySearch() {
    this.loadAllMaterials();
  }

  onSearch() {
    this.loadAllMaterials();
  }

  onFileSelected(event: any) {
    const file = event.target.files[0];
    if (file) {
      this.selectedFile = file;
    }
  }

  openUploadModal() {
    this.materialTitle = '';
    this.selectedFile = null;
    this.error = '';
    this.success = '';
    this.selectedCourseId = this.courses.length > 0 ? this.courses[0].courseId : null;
    this.showModal = true;
    this.cdr.detectChanges();
  }

  closeModal() {
    this.showModal = false;
    this.cdr.detectChanges();
  }

  uploadMaterial() {
    if (!this.selectedCourseId || !this.materialTitle.trim() || !this.selectedFile) {
      this.error = 'Please fill out all fields and select a file to upload.';
      return;
    }

    this.uploading = true;
    this.error = '';
    this.success = '';
    this.cdr.detectChanges();

    const requestBlob = new Blob([JSON.stringify({
      title: this.materialTitle.trim(),
      courseId: Number(this.selectedCourseId)
    })], { type: 'application/json' });

    const formData = new FormData();
    formData.append('course-material', requestBlob);
    formData.append('file', this.selectedFile);

    this.http.post<any>('/api/course-material', formData).subscribe({
      next: () => {
        this.materialTitle = '';
        this.selectedFile = null;
        this.success = 'Material uploaded successfully!';
        this.showModal = false;
        this.loadAllMaterials();
        this.uploading = false;
        this.cdr.detectChanges();
      },
      error: (err) => {
        console.error('Upload failed:', err);
        let errMsg = 'Failed to upload material to the database.';
        if (err.error) {
          if (typeof err.error === 'string') {
            errMsg = err.error;
          } else if (Array.isArray(err.error)) {
            errMsg = err.error.join(', ');
          } else if (err.error.message) {
            errMsg = err.error.message;
          }
        } else if (err.message) {
          errMsg = err.message;
        }
        this.error = errMsg;
        this.uploading = false;
        this.cdr.detectChanges();
      }
    });
  }

  deleteMaterial(materialId: number) {
    if (confirm('Are you sure you want to delete this course material?')) {
      this.http.delete(`/api/course-material/${materialId}/delete-cm`, { responseType: 'text' }).subscribe({
        next: () => {
          this.success = 'Material deleted successfully!';
          this.loadAllMaterials();
        },
        error: (err) => {
          console.error('Delete failed:', err);
          this.error = 'Failed to delete course material.';
          this.cdr.detectChanges();
        }
      });
    }
  }

  downloadMaterial(materialId: number, fileName: string) {
    this.http.get(`/api/course-material/${materialId}/download-cm`, { responseType: 'blob' }).subscribe({
      next: (blob) => {
        const url = window.URL.createObjectURL(blob);
        const a = document.createElement('a');
        a.href = url;
        a.download = fileName || 'download';
        document.body.appendChild(a);
        a.click();
        document.body.removeChild(a);
        window.URL.revokeObjectURL(url);
      },
      error: (err) => {
        console.error('Download failed:', err);
      }
    });
  }
}
