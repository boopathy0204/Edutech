import { Component, OnInit } from '@angular/core';
import { Router, RouterOutlet, RouterLink, RouterLinkActive } from '@angular/router';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AuthService } from '../auth.service';
import { AcademicPeriodService, AcademicPeriod } from '../academic-period.service';

interface NavItem {
  label: string;
  route: string;
}

@Component({
  selector: 'app-layout',
  imports: [RouterOutlet, RouterLink, RouterLinkActive, CommonModule, FormsModule],
  templateUrl: './layout.html',
  styleUrl: './layout.css'
})
export class Layout implements OnInit {
  username = '';
  role = '';
  navItems: NavItem[] = [];
  periods: AcademicPeriod[] = [];
  selectedPeriod: AcademicPeriod | null = null;

  constructor(
    private authService: AuthService,
    private router: Router,
    private academicPeriodService: AcademicPeriodService
  ) {}

  ngOnInit() {
    this.username = this.authService.getUsername() || 'User';
    this.role = this.authService.getRole() || '';
    this.generateNavItems();

    // Fetch all periods for dropdown
    this.academicPeriodService.getAllPeriods().subscribe(data => {
      this.periods = data;
    });

    // Get active period
    this.academicPeriodService.getActivePeriod().subscribe();

    // Subscribe to selection changes
    this.academicPeriodService.selectedPeriod$.subscribe(period => {
      this.selectedPeriod = period;
    });
  }

  comparePeriods(p1: AcademicPeriod | null, p2: AcademicPeriod | null): boolean {
    return p1 && p2 ? p1.academicPeriodId === p2.academicPeriodId : p1 === p2;
  }

  onPeriodChange(period: AcademicPeriod) {
    this.academicPeriodService.setSelectedPeriod(period);
  }

  generateNavItems() {
    if (this.role === 'ADMIN') {
      this.navItems = [
        { label: 'Dashboard', route: '/dashboard' },
        { label: 'Manage Users', route: '/admin/users' },
        { label: 'Manage Professors', route: '/admin/professors' },
        { label: 'Manage Students', route: '/admin/students' },
        { label: 'Manage Admin Staff', route: '/admin/staff' }
      ];
    } else if (this.role === 'PROFESSOR') {
      this.navItems = [
        { label: 'My Profile', route: '/profile' },
        { label: 'My Courses', route: '/professor/courses' },
        { label: 'Course Materials', route: '/professor/materials' },
        { label: 'Assignments', route: '/professor/assignments' },
        { label: 'Student Submissions', route: '/professor/submissions' },
        { label: 'Grade Center', route: '/professor/grades' }
      ];
    } else if (this.role === 'STUDENT') {
      this.navItems = [
        { label: 'My Profile', route: '/profile' },
        { label: 'My Courses', route: '/courses' },
        { label: 'Course Registration', route: '/enrollment' },
        { label: 'My Grades', route: '/grades' },
        { label: 'Academic Record', route: '/academic-record' },
        { label: 'Notifications', route: '/notifications' },
        { label: 'My Schedule', route: '/schedule' }
      ];
    }
  }

  logout() {
    this.authService.logout();
    this.router.navigate(['/login']);
  }
}
