import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-course-materials',
  imports: [CommonModule, RouterLink],
  templateUrl: './course-materials.html',
  styleUrl: './course-materials.css'
})
export class CourseMaterials implements OnInit {
  courseId!: number;
  course: any = null;
  materials: any[] = [];
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
        this.loadMaterials();
      }
    });
  }
  downloadMaterial(id: number) {
    this.http.get(`/api/course-material/${id}/download-cm`, {
      responseType: 'blob'
    }).subscribe(blob => {
      const url = window.URL.createObjectURL(blob);
  
      const a = document.createElement('a');
      a.href = url;
      a.download = 'download';
      a.click();
  
      window.URL.revokeObjectURL(url);
    });
  }
  loadMaterials() {
    this.loading = true;
    this.error = '';
    this.cdr.detectChanges();

    // Fetch uploaded materials for this course
    this.http.get<any[]>(`/api/course-material/${this.courseId}/cm-listbycourse`).subscribe({
      next: (materialsList) => {
        this.materials = materialsList;
        if (materialsList.length > 0) {
          this.course = {
            courseName: materialsList[0].courseName,
            courseCode: 'N/A'
          };
        }
        this.loading = false;
        this.cdr.detectChanges();
      },
      error: (err) => {
        console.error('Failed to load course materials:', err);
        this.error = 'Failed to load uploaded course materials.';
        this.loading = false;
        this.cdr.detectChanges();
      }
    });
  }
}
