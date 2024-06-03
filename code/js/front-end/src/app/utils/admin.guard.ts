import {Injectable} from '@angular/core';
import {Router} from '@angular/router';

@Injectable({
  providedIn: 'root'
})
export class AdminGuard {
  constructor(private router: Router) {
  }

  canActivate() {
    if (localStorage.getItem('role') === 'ADMIN') {
      return true;
    }
    this.router.navigate(['/work']);
    return false;
  }
}
