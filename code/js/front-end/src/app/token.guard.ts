import {Injectable} from '@angular/core';
import {Router} from '@angular/router';
import {AuthService} from "./auth/auth.service";
import {HttpClient} from "@angular/common/http";

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
      this.router.navigate(['/login']) // Redirect to login if token doesn't exist
    } else {
      // Check if token is valid
      /*this.authService.checkToken().subscribe(res => {
        if (!res.valid) {
          localStorage.clear()
          this.router.navigate(['/login'])
        } else {
          ret = true
        }
      })*/
      ret = true
    }
    return ret
  }
}
