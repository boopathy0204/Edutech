import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { ActivatedRoute, RouterLink, Router } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AcademicPeriodService } from '../../academic-period.service';

@Component({
  selector: 'app-professor-course-details',
  standalone: true,
  imports: [CommonModule, RouterLink, FormsModule],
  templateUrl: './course-details.html',
  styleUrl: './course-details.css'
})
export class ProfessorCourseDetails implements OnInit {
  initialPeriodId: number | null = null;
  courseId!: number;
  course: any = null;
  loading = false;
  error = '';
  success = '';
  
  // Tabs configuration
  activeTab: 'overview' | 'schedule' | 'students' | 'assignments' | 'materials' = 'overview';
  
  // Data lists
  studentsList: any[] = [];
  scheduleList: any[] = [];
  assignmentsList: any[] = [];
  materialsList: any[] = [];

  // Schedule Modal variables
  showScheduleModal = false;
  savingSchedule = false;
  formDay = 'MONDAY';
  formStartTime = '';
  formEndTime = '';
  formRoomNumber = '';

  // Material Modal variables
  showMaterialModal = false;
  uploadingMaterial = false;
  formMaterialTitle = '';
  formMaterialFile: File | null = null;

  studentSearchQuery = '';

  constructor(
    private route: ActivatedRoute,
    private http: HttpClient,
    private cdr: ChangeDetectorRef,
    private router: Router,
    private academicPeriodService: AcademicPeriodService
  ) {}

  ngOnInit() {
    this.route.paramMap.subscribe(params => {
      const idParam = params.get('courseId');
      if (idParam) {
        this.courseId = +idParam;
        this.loadCourseDetails();
      }
    });

    this.academicPeriodService.selectedPeriod$.subscribe(period => {
      if (period) {
        if (this.initialPeriodId === null) {
          this.initialPeriodId = period.academicPeriodId;
        } else if (this.initialPeriodId !== period.academicPeriodId) {
          // Redirect to professor course list when period changes
          this.router.navigate(['/professor/courses']);
        }
      }
    });
  }

  loadCourseDetails() {
    this.loading = true;
    this.error = '';
    this.cdr.detectChanges();

    this.http.get<any>(`/api/course/${this.courseId}/course-details`).subscribe({
      next: (data) => {
        this.course = {
          ...data,
          department: 'Computer Science',
          studentsCount: 0,
          assignmentsCount: 0
        };

        // 1. Fetch live schedule details
        this.loadScheduleList();

        // 2. Fetch live assignments details
        this.loadAssignmentsList();

        // 3. Fetch live enrolled students list
        this.loadStudentsList();

        // 4. Fetch live uploaded materials
        this.loadMaterialsList();

        this.loading = false;
        this.cdr.detectChanges();
      },
      error: (err) => {
        console.error('Failed to load course details:', err);
        this.error = 'Course not found or database connection failed.';
        this.loading = false;
        this.cdr.detectChanges();
      }
    });
  }

  loadScheduleList() {
    this.http.get<any[]>(`/api/schedule/${this.courseId}/schedule-listbycourse`).subscribe({
      next: (sched) => {
        this.scheduleList = sched;
        this.cdr.detectChanges();
      },
      error: (err) => {
        console.error('Failed to query schedule:', err);
        this.scheduleList = [];
        this.cdr.detectChanges();
      }
    });
  }

  loadAssignmentsList() {
    this.http.get<any[]>(`/api/assignment/${this.courseId}/course-list`).subscribe({
      next: (assList) => {
        this.assignmentsList = assList;
        if (this.course) {
          this.course.assignmentsCount = assList.length;
        }
        this.cdr.detectChanges();
      },
      error: (err) => {
        console.error('Failed to query assignments:', err);
        this.assignmentsList = [];
        this.cdr.detectChanges();
      }
    });
  }

  loadStudentsList() {
    let url = `/api/enrollment/${this.courseId}/student-listbycourse`;
    if (this.studentSearchQuery.trim()) {
      url += `?query=${encodeURIComponent(this.studentSearchQuery.trim())}`;
    }
    this.http.get<any[]>(url).subscribe({
      next: (stud) => {
        this.studentsList = stud;
        if (this.course && !this.studentSearchQuery.trim()) {
          this.course.studentsCount = stud.length;
        }
        this.cdr.detectChanges();
      },
      error: (err) => {
        console.error('Failed to query enrolled students:', err);
        this.studentsList = [];
        this.cdr.detectChanges();
      }
    });
  }

  onStudentSearch() {
    this.loadStudentsList();
  }

  loadMaterialsList() {
    this.http.get<any[]>(`/api/course-material/${this.courseId}/cm-listbycourse`).subscribe({
      next: (mats) => {
        this.materialsList = mats;
        this.cdr.detectChanges();
      },
      error: (err) => {
        console.error('Failed to query materials list:', err);
        this.materialsList = [];
        this.cdr.detectChanges();
      }
    });
  }

  setTab(tab: 'overview' | 'schedule' | 'students' | 'assignments' | 'materials') {
    this.activeTab = tab;
    this.cdr.detectChanges();
  }

  // --- SCHEDULE ACTIONS ---
  openScheduleModal() {
    this.error = '';
    this.success = '';
    this.formDay = 'MONDAY';
    this.formStartTime = '';
    this.formEndTime = '';
    this.formRoomNumber = '';
    this.showScheduleModal = true;
    this.cdr.detectChanges();
  }

  closeScheduleModal() {
    this.error = '';
    this.showScheduleModal = false;
    this.cdr.detectChanges();
  }

  saveSchedule() {
    this.error = '';
    this.success = '';
    
    if (!this.formStartTime || !this.formEndTime || !this.formRoomNumber.trim()) {
      this.error = 'Please fill out all required schedule fields.';
      return;
    }

    this.savingSchedule = true;
    this.cdr.detectChanges();

    // Map time strings to HH:mm:ss format
    const formatTime = (t: string) => t.length === 5 ? `${t}:00` : t;

    const payload = {
      courseId: Number(this.courseId),
      day: this.formDay,
      startTime: formatTime(this.formStartTime),
      endTime: formatTime(this.formEndTime),
      roomNumber: this.formRoomNumber.trim()
    };

    this.http.post('/api/schedule', payload, { responseType: 'text' }).subscribe({
      next: () => {
        this.success = 'Schedule added successfully!';
        this.loadScheduleList();
        this.savingSchedule = false;
        this.showScheduleModal = false;
        this.cdr.detectChanges();
      },
      error: (err) => {
        console.error('Failed to save schedule:', err);
        const errMsg = (typeof err.error === 'string') 
          ? err.error 
          : (err.error?.message || err.message || 'Failed to save schedule conflict check. Make sure there are no overlaps.');
        this.error = errMsg;
        this.savingSchedule = false;
        this.cdr.detectChanges();
      }
    });
  }

  deleteSchedule(scheduleId: number) {
    if (confirm('Are you sure you want to delete this schedule slot?')) {
      this.http.delete(`/api/schedule/${scheduleId}/delete`, { responseType: 'text' }).subscribe({
        next: () => {
          this.success = 'Schedule slot deleted successfully!';
          this.loadScheduleList();
        },
        error: (err) => {
          console.error('Failed to delete schedule:', err);
          this.error = 'Failed to delete schedule record from database.';
          this.cdr.detectChanges();
        }
      });
    }
  }

  // --- MATERIAL ACTIONS ---
  openMaterialModal() {
    this.formMaterialTitle = '';
    this.formMaterialFile = null;
    this.showMaterialModal = true;
    this.cdr.detectChanges();
  }

  closeMaterialModal() {
    this.showMaterialModal = false;
    this.cdr.detectChanges();
  }

  onMaterialFileSelected(event: any) {
    const file = event.target.files[0];
    if (file) {
      this.formMaterialFile = file;
    }
  }

  uploadMaterial() {
    if (!this.formMaterialTitle.trim() || !this.formMaterialFile) {
      alert('Please fill out the title and select a file.');
      return;
    }

    this.uploadingMaterial = true;
    this.cdr.detectChanges();

    const requestBlob = new Blob([JSON.stringify({
      title: this.formMaterialTitle.trim(),
      courseId: Number(this.courseId)
    })], { type: 'application/json' });

    const formData = new FormData();
    formData.append('course-material', requestBlob);
    formData.append('file', this.formMaterialFile);

    this.http.post<any>('/api/course-material', formData).subscribe({
      next: () => {
        this.success = 'Material uploaded successfully!';
        this.loadMaterialsList();
        this.uploadingMaterial = false;
        this.showMaterialModal = false;
        this.cdr.detectChanges();
      },
      error: (err) => {
        console.error('Failed to upload material:', err);
        alert(err.error?.message || 'Failed to upload material.');
        this.uploadingMaterial = false;
        this.cdr.detectChanges();
      }
    });
  }

  deleteMaterial(materialId: number) {
    if (confirm('Are you sure you want to delete this material?')) {
      this.http.delete(`/api/course-material/${materialId}/delete-cm`, { responseType: 'text' }).subscribe({
        next: () => {
          this.success = 'Material deleted successfully!';
          this.loadMaterialsList();
        },
        error: (err) => {
          console.error('Failed to delete material:', err);
          this.error = 'Failed to delete material from database.';
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
