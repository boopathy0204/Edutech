import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { map, switchMap } from 'rxjs/operators';
import { of } from 'rxjs';
import { AuthService } from './auth.service';

export const authGuard: CanActivateFn = (route, state) => {
  const authService = inject(AuthService);
  const router = inject(Router);

  return authService.rehydrate().pipe(
    switchMap(isLoggedIn => {
      // 1. Check if authenticated
      if (!isLoggedIn || !authService.isAuthenticated()) {
        router.navigate(['/login']);
        return of(false);
      }

      // 2. Check if user is forced to change password
      if (authService.getMustChangePassword()) {
        if (state.url !== '/change-password') {
          router.navigate(['/change-password']);
          return of(false);
        }
        return of(true);
      }

      // 3. Check profile completeness for Student & Professor onboarding
      return authService.checkProfileComplete().pipe(
        map(isComplete => {
          if (!isComplete) {
            if (state.url !== '/complete-profile') {
              router.navigate(['/complete-profile']);
              return false;
            }
            return true;
          }

          // If complete but visiting complete-profile page, redirect to correct landing page
          if (state.url === '/complete-profile') {
            if (authService.getRole() === 'ADMIN') {
              router.navigate(['/dashboard']);
            } else {
              router.navigate(['/profile']);
            }
            return false;
          }

          // 4. Check role requirements
          const requiredRoles = route.data?.['roles'] as string[];
          if (requiredRoles && requiredRoles.length > 0) {
            const userRole = authService.getRole();
            if (!userRole || !requiredRoles.includes(userRole)) {
              router.navigate(['/403']);
              return false;
            }
          }

          return true;
        })
      );
    })
  );
};
