import { Routes } from '@angular/router';
import { Login } from './login/login';
import { Layout } from './layout/layout';
import { Dashboard } from './dashboard/dashboard';
import { NotAuthorized } from './not-authorized/not-authorized';
import { ChangePassword } from './change-password';
import { Users } from './admin/users/users';
import { Students } from './admin/students/students';
import { Professors } from './admin/professors/professors';
import { Staff } from './admin/staff/staff';
import { UserDetails } from './admin/user-details/user-details';
import { MyProfile } from './profile/profile';
import { MyCourses } from './courses/courses';
import { CourseDetails } from './courses/course-details';
import { CourseMaterials } from './courses/course-materials';
import { MyAssignments } from './courses/my-assignments';
import { AssignmentDetails } from './courses/assignment-details';
import { SubmitAssignment } from './courses/submit-assignment';
import { CourseEnrollment } from './courses/course-enrollment';
import { AcademicRecord } from './academic-record/academic-record';
import { MyNotifications } from './notifications/notifications';
import { MyGrades } from './grades/grades';
import { MySchedule } from './schedule/schedule';
import { CompleteProfile } from './onboarding/complete-profile';
import { authGuard } from './auth.guard';
import { ProfessorCourses } from './professor/courses/courses';
import { ProfessorCourseDetails } from './professor/course-details/course-details';
import { ProfessorMaterials } from './professor/materials/materials';
import { ProfessorAssignments } from './professor/assignments/assignments';
import { ProfessorSubmissions } from './professor/submissions/submissions';
import { ProfessorGrades } from './professor/grades/grades';
import { ProfessorNotifications } from './professor/notifications/notifications';
import { ProfessorAssignmentSubmissions } from './professor/assignment-submissions/assignment-submissions';




export const routes: Routes = [
  { path: 'login', component: Login },
  { 
    path: 'change-password', 
    component: ChangePassword,
    canActivate: [authGuard]
  },
  { 
    path: 'complete-profile', 
    component: CompleteProfile,
    canActivate: [authGuard]
  },
  { path: '403', component: NotAuthorized },
  {
    path: '',
    component: Layout,
    canActivate: [authGuard],
    children: [
      { 
        path: 'dashboard', 
        component: Dashboard,
        data: { roles: ['ADMIN'] }
      },
      { 
        path: 'profile', 
        component: MyProfile,
        data: { roles: ['ADMIN', 'PROFESSOR', 'STUDENT'] }
      },
      { 
        path: 'courses', 
        component: MyCourses,
        data: { roles: ['ADMIN', 'PROFESSOR', 'STUDENT'] }
      },
      { 
        path: 'courses/:courseId', 
        component: CourseDetails,
        data: { roles: ['ADMIN', 'PROFESSOR', 'STUDENT'] }
      },
      { 
        path: 'courses/:courseId/materials', 
        component: CourseMaterials,
        data: { roles: ['ADMIN', 'PROFESSOR', 'STUDENT'] }
      },
      { 
        path: 'courses/:courseId/assignments', 
        component: MyAssignments,
        data: { roles: ['ADMIN', 'PROFESSOR', 'STUDENT'] }
      },
      { 
        path: 'courses/:courseId/assignments/:assignmentId', 
        component: AssignmentDetails,
        data: { roles: ['ADMIN', 'PROFESSOR', 'STUDENT'] }
      },
      { 
        path: 'courses/:courseId/assignments/:assignmentId/submit', 
        component: SubmitAssignment,
        data: { roles: ['ADMIN', 'PROFESSOR', 'STUDENT'] }
      },
      { 
        path: 'enrollment', 
        component: CourseEnrollment,
        data: { roles: ['ADMIN', 'PROFESSOR', 'STUDENT'] }
      },
      { 
        path: 'academic-record', 
        component: AcademicRecord,
        data: { roles: ['ADMIN', 'PROFESSOR', 'STUDENT'] }
      },
      { 
        path: 'notifications', 
        component: MyNotifications,
        data: { roles: ['ADMIN', 'PROFESSOR', 'STUDENT'] }
      },
      { 
        path: 'grades', 
        component: MyGrades,
        data: { roles: ['ADMIN', 'PROFESSOR', 'STUDENT'] }
      },
      { 
        path: 'schedule', 
        component: MySchedule,
        data: { roles: ['ADMIN', 'PROFESSOR', 'STUDENT'] }
      },
      { 
        path: 'professor/courses', 
        component: ProfessorCourses,
        data: { roles: ['PROFESSOR'] }
      },
      { 
        path: 'professor/courses/:courseId', 
        component: ProfessorCourseDetails,
        data: { roles: ['PROFESSOR'] }
      },
      { 
        path: 'professor/materials', 
        component: ProfessorMaterials,
        data: { roles: ['PROFESSOR'] }
      },
      { 
        path: 'professor/assignments', 
        component: ProfessorAssignments,
        data: { roles: ['PROFESSOR'] }
      },
      { 
        path: 'professor/submissions', 
        component: ProfessorSubmissions,
        data: { roles: ['PROFESSOR'] }
      },
      { 
        path: 'professor/assignment-submissions/:assignmentId', 
        component: ProfessorAssignmentSubmissions,
        data: { roles: ['PROFESSOR'] }
      },
      { 
        path: 'professor/grades', 
        component: ProfessorGrades,
        data: { roles: ['PROFESSOR'] }
      },
      { 
        path: 'professor/notifications', 
        component: ProfessorNotifications,
        data: { roles: ['PROFESSOR'] }
      },
      { 
        path: 'admin/users', 
        component: Users,
        data: { roles: ['ADMIN'] }
      },
      { 
        path: 'admin/users/:userId/details', 
        component: UserDetails,
        data: { roles: ['ADMIN'] }
      },
      { 
        path: 'admin/professors', 
        component: Professors,
        data: { roles: ['ADMIN'] }
      },
      { 
        path: 'admin/students', 
        component: Students,
        data: { roles: ['ADMIN'] }
      },
      { 
        path: 'admin/staff', 
        component: Staff,
        data: { roles: ['ADMIN'] }
      },
      { path: '', redirectTo: 'profile', pathMatch: 'full' }
    ]
  },
  { path: '**', redirectTo: 'profile' }
];
