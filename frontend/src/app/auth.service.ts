import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, tap, catchError, map, of } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private readonly TOKEN_KEY = 'auth_token';
  private readonly ROLE_KEY = 'auth_role';
  private readonly USERNAME_KEY = 'auth_username';
  private readonly MUST_CHANGE_PASSWORD_KEY = 'auth_must_change_password';

  currentUser: any = null;
  private studentProfileCache: any = null;
  private professorProfileCache: any = null;

  constructor(private http: HttpClient) {}

  login(username: string, password: string): Observable<any> {
    return this.http.post<any>('/api/auth/login', { username, password }).pipe(
      tap(response => {
        if (response && response.token) {
          sessionStorage.setItem('auth_profile_complete', String(response.profileComplete));
          sessionStorage.setItem(this.TOKEN_KEY, response.token);
          sessionStorage.setItem(this.ROLE_KEY, response.role);
          sessionStorage.setItem(this.USERNAME_KEY, response.username);
          sessionStorage.setItem(this.MUST_CHANGE_PASSWORD_KEY, String(response.mustChangePassword));
          this.currentUser = {
            userId: response.userId,
            username: response.username,
            role: response.role,
            mustChangePassword: response.mustChangePassword,
            profileComplete: response.profileComplete
          };
        }
      })
    );
  }

  getCurrentUser(): Observable<any> {
    return this.http.get<any>('/api/user/me');
  }

  getStudentProfile(): Observable<any> {
    if (this.studentProfileCache) {
      return of(this.studentProfileCache);
    }
    return this.http.get<any>('/api/student/student-profile').pipe(
      tap(profile => this.studentProfileCache = profile)
    );
  }

  getProfessorProfile(): Observable<any> {
    if (this.professorProfileCache) {
      return of(this.professorProfileCache);
    }
    return this.http.get<any>('/api/professor/professorprofile').pipe(
      tap(profile => this.professorProfileCache = profile)
    );
  }

  checkProfileComplete(): Observable<boolean> {
    const cachedComplete = sessionStorage.getItem('auth_profile_complete') === 'true';
    return of(cachedComplete);
  }

  rehydrate(): Observable<boolean> {
    const token = this.getToken();
    if (token && !this.currentUser) {
      return this.getCurrentUser().pipe(
        tap(user => {
          this.currentUser = user;
          sessionStorage.setItem(this.ROLE_KEY, user.role);
          sessionStorage.setItem(this.USERNAME_KEY, user.username);
          sessionStorage.setItem(this.MUST_CHANGE_PASSWORD_KEY, String(user.mustChangePassword));
          sessionStorage.setItem('auth_profile_complete', String(user.profileComplete));
        }),
        map(() => true),
        catchError(() => {
          this.logout();
          return of(false);
        })
      );
    }
    return of(true);
  }

  getToken(): string | null {
    return sessionStorage.getItem(this.TOKEN_KEY);
  }

  getRole(): string | null {
    return sessionStorage.getItem(this.ROLE_KEY);
  }

  getUsername(): string | null {
    return sessionStorage.getItem(this.USERNAME_KEY);
  }

  getMustChangePassword(): boolean {
    return sessionStorage.getItem(this.MUST_CHANGE_PASSWORD_KEY) === 'true';
  }

  isAuthenticated(): boolean {
    return !!this.getToken();
  }

  logout() {
    sessionStorage.removeItem(this.TOKEN_KEY);
    sessionStorage.removeItem(this.ROLE_KEY);
    sessionStorage.removeItem(this.USERNAME_KEY);
    sessionStorage.removeItem(this.MUST_CHANGE_PASSWORD_KEY);
    sessionStorage.removeItem('auth_profile_complete');
    this.currentUser = null;
    this.studentProfileCache = null;
    this.professorProfileCache = null;
  }
}
