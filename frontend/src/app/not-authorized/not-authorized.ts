import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '../auth.service';

@Component({
  selector: 'app-not-authorized',
  imports: [],
  templateUrl: './not-authorized.html',
  styleUrl: './not-authorized.css'
})
export class NotAuthorized {
  constructor(private authService: AuthService, private router: Router) {}

  goToDashboard() {
    // For now, all roles go to the dashboard wrapper route '/dashboard'
    this.router.navigate(['/dashboard']);
  }
}
