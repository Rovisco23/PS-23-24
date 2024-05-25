import { Injectable } from '@angular/core';
import {Router, ActivatedRouteSnapshot, RouterStateSnapshot} from '@angular/router';
import { OriginalUrlService } from './originalUrl.service';

@Injectable({
  providedIn: 'root'
})
export class AuthGuard {
  constructor(private router: Router, private originalUrlService: OriginalUrlService) {}

  canActivate() {
    if (localStorage.getItem('token')) {
      return true;
    }
    const originalUrl = this.router.getCurrentNavigation()?.finalUrl?.toString();
    if (originalUrl) {
      this.originalUrlService.setOriginalUrl(originalUrl);
    }
    this.router.navigate(['/login']);
    return false;
  }
}
