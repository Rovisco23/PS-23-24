import {Injectable} from '@angular/core';
import {Router} from '@angular/router';

@Injectable({
  providedIn: 'root'
})
export class TokenGuard {

  constructor(private router: Router) {
  }

  canActivate(): boolean {
    // Check if token cookie is present (You may implement your own logic here)
    const tokenExists = localStorage.getItem('token')
    let ret = false

    if (!tokenExists) {
      this.router.navigate(['/login']) // Redirect to login component if token doesn't exist
    } else {
      ret = true
    }
    return ret
  }
}
