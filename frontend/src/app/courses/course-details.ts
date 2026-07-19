import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-course-details',
  imports: [CommonModule, RouterLink],
  templateUrl: './course-details.html',
  styleUrl: './course-details.css'
})
export class CourseDetails implements OnInit {
  courseId!: number;
  course: any = null;
  materials: any[] = [];
  assignments: any[] = [];
  loading = false;
  error = '';

  constructor(
    private route: ActivatedRoute,
    private http: HttpClient,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit() {
    this.route.paramMap.subscribe(params => {
      const idParam = params.get('courseId');
      if (idParam) {
        this.courseId = +idParam;
        this.loadCourseDetails();
      }
    });
  }

  loadCourseDetails() {
    this.loading = true;
    this.error = '';
    this.cdr.detectChanges();

    // Fetch course main metadata
    this.http.get<any>(`/api/course/${this.courseId}/course-details`).subscribe({
      next: (courseData) => {
        this.course = courseData;

        // Fetch course syllabus files in parallel
        this.http.get<any[]>(`/api/course-material/${this.courseId}/cm-listbycourse`).subscribe({
          next: (materialsList) => {
            this.materials = materialsList;
            
            // Fetch course assignments list
            this.http.get<any[]>(`/api/assignment/${this.courseId}/course-list`).subscribe({
              next: (assignmentsList) => {
                this.assignments = assignmentsList;
                this.loading = false;
                this.cdr.detectChanges();
              },
              error: (err) => {
                console.error('Failed to load assignments list:', err);
                this.loading = false;
                this.cdr.detectChanges();
              }
            });
          },
          error: (err) => {
            console.error('Failed to load materials list:', err);
            this.loading = false;
            this.cdr.detectChanges();
          }
        });
      },
      error: (err) => {
        console.error('Failed to load course details:', err);
        this.error = 'Course details not found or failed to load.';
        this.loading = false;
        this.cdr.detectChanges();
      }
    });
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
