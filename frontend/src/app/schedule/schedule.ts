import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { CommonModule } from '@angular/common';
import { switchMap } from 'rxjs/operators';
import { AuthService } from '../auth.service';
import { AcademicPeriodService } from '../academic-period.service';

@Component({
  selector: 'app-schedule',
  imports: [CommonModule],
  templateUrl: './schedule.html',
  styleUrl: './schedule.css'
})
export class MySchedule implements OnInit {
  student: any = null;
  daysOfWeek = ['MONDAY', 'TUESDAY', 'WEDNESDAY', 'THURSDAY', 'FRIDAY', 'SATURDAY', 'SUNDAY'];
  scheduleByDay: { [key: string]: any[] } = {};
  loading = false;
  error = '';
  selectedPeriodId: number | null = null;

  constructor(
    private http: HttpClient,
    private authService: AuthService,
    private cdr: ChangeDetectorRef,
    private academicPeriodService: AcademicPeriodService
  ) {}

  ngOnInit() {
    this.academicPeriodService.selectedPeriod$.subscribe(period => {
      if (period) {
        this.selectedPeriodId = period.academicPeriodId;
        this.loadWeeklySchedule();
      }
    });
  }

  loadWeeklySchedule() {
    this.loading = true;
    this.error = '';
    this.daysOfWeek.forEach(day => this.scheduleByDay[day] = []);
    this.cdr.detectChanges();

    this.authService.getStudentProfile().pipe(
      switchMap(studentData => {
        this.student = studentData;
        let url = `/api/schedule/student/${studentData.studentId}/weekly-schedule`;
        if (this.selectedPeriodId) {
          url += `?academicPeriodId=${this.selectedPeriodId}`;
        }
        return this.http.get<any[]>(url);
      })
    ).subscribe({
      next: (schedules: any[]) => {
        // Group schedules by day of the week
        schedules.forEach(schedule => {
          const day = schedule.day ? schedule.day.toUpperCase() : '';
          if (this.scheduleByDay[day]) {
            this.scheduleByDay[day].push(schedule);
          }
        });

        // Sort schedules on each day by start time
        this.daysOfWeek.forEach(day => {
          this.scheduleByDay[day].sort((a, b) => {
            if (!a.startTime || !b.startTime) return 0;
            return a.startTime.localeCompare(b.startTime);
          });
        });

        this.loading = false;
        this.cdr.detectChanges();
      },
      error: (err) => {
        console.error('Failed to load schedule data:', err);
        this.error = 'Failed to load class schedule timings.';
        this.loading = false;
        this.cdr.detectChanges();
      }
    });
  }

  hasSchedule(day: string): boolean {
    return this.scheduleByDay[day] && this.scheduleByDay[day].length > 0;
  }
}
