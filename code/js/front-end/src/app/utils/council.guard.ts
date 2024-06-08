import {Injectable} from '@angular/core';
import {Router} from '@angular/router';

@Injectable({
  providedIn: 'root'
})
export class CouncilGuard {
  constructor(private router: Router) {
  }

  canActivate() {
    if (localStorage.getItem('role') === 'ADMIN' || localStorage.getItem('role') === 'CÂMARA') {
      return true;
    }
    this.router.navigate(['/work']);
    return false;
  }
}
