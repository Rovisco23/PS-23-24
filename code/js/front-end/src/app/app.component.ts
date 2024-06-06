import {Component, ViewChild} from '@angular/core';
import {NavigationEnd, Router, RouterLink, RouterLinkActive, RouterModule} from '@angular/router';
import {MatToolbarModule} from "@angular/material/toolbar";
import {MatDrawer, MatSidenavModule} from "@angular/material/sidenav";
import {MatIconModule} from "@angular/material/icon";
import {MatButton, MatIconButton} from "@angular/material/button";
import {NgClass, NgIf, NgOptimizedImage} from "@angular/common";
import {filter} from 'rxjs/operators';
import {HttpService} from "./utils/http.service";
import {HttpClientModule} from "@angular/common/http";
import {MatBadge} from "@angular/material/badge";
import {catchError, throwError} from "rxjs";
import {ErrorHandler} from "./utils/errorHandle";

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [
    RouterModule,
    MatToolbarModule,
    MatSidenavModule,
    MatIconModule,
    RouterLink,
    RouterLinkActive,
    MatIconButton,
    MatButton,
    HttpClientModule,
    NgIf,
    NgOptimizedImage,
    MatBadge,
    NgClass
  ],
  templateUrl: './app.component.html',
  styleUrl: './app.component.css',
  providers: [HttpService],
})
export class AppComponent {
  title = 'livro-de-obra-eletronico';
  showLayout = true;
  src = ''
  notification = 0;
  @ViewChild('drawer') drawer: MatDrawer | undefined;

  logout(): void {
    const token = localStorage.getItem('token');
    localStorage.removeItem('userId');
    localStorage.removeItem('token');
    localStorage.removeItem('role');
    localStorage.removeItem('profilePicture');
    this.httpService.logout(token ?? '').subscribe(() => {
      console.log("Logout Done")
      this.router.navigate(['/login']);
    })
  }

  constructor(private router: Router, private httpService: HttpService, private errorHandle: ErrorHandler) {
    this.router.events
      .pipe(filter((events) => events instanceof NavigationEnd))
      .subscribe((event: any) => {
        const navigationEndEvent = event as NavigationEnd;
        this.showLayout = navigationEndEvent.urlAfterRedirects !== '/login' &&
          navigationEndEvent.urlAfterRedirects !== '/signup';
        if (localStorage.getItem('token')){
          this.httpService.getProfilePicture().pipe(
            catchError(error => {
              this.errorHandle.handleError(error);
              return throwError(error);
            })
          ).subscribe((data) => {
            if (data.size === 0) {
              this.src = './assets/profile.png'
            } else {
              localStorage.setItem('profilePicture', URL.createObjectURL(data))
              this.src = URL.createObjectURL(data)
            }
          })
          this.httpService.getInviteList().pipe(
            catchError(error => {
              this.errorHandle.handleError(error);
              return throwError(error);
            })
          ).subscribe(res => {
            this.notification = res.length;
          })
        }
      })
  }

  toggleDrawer() {
    if (this.drawer) {
      this.drawer.toggle();
    }
  }

  checkAdminRole() {
    return localStorage.getItem('role') === 'ADMIN'
  }

  onProfileCall() {
    this.router.navigate(['/profile']);
  }
}
