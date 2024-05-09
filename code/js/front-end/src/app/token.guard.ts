import { Injectable } from '@angular/core';
import { Router } from '@angular/router';

@Injectable({
  providedIn: 'root'
})
export class TokenGuard {

  constructor(private router: Router) {}

  canActivate(): boolean {
    // Check if token cookie is present (You may implement your own logic here)
    const tokenExists = document.cookie.includes('token');

    if (!tokenExists) {
      this.router.navigate(['/login']); // Redirect to login if token doesn't exist
      return false;
    }

    return true;
  }
}
