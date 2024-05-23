import {Component} from '@angular/core';
import {NavigationEnd, Router, RouterModule} from '@angular/router';
import {MatToolbarModule} from "@angular/material/toolbar";
import {MatSidenavModule} from "@angular/material/sidenav";
import {MatIconModule} from "@angular/material/icon";
import {RouterLink, RouterLinkActive} from "@angular/router";
import {MatButton, MatIconButton} from "@angular/material/button";
import {NgIf, NgOptimizedImage} from "@angular/common";
import {filter} from 'rxjs/operators';
import {HttpService} from "./utils/http.service";
import {HttpClientModule} from "@angular/common/http";

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
    NgOptimizedImage
  ],
  templateUrl: './app.component.html',
  styleUrl: './app.component.css',
  providers: [HttpService],
})
export class AppComponent {
  title = 'livro-de-obra-eletronico';
  showLayout = true;
  src = ''

  logout(): void {
    this.httpService.logout(localStorage.getItem('token')?? '').subscribe(() => {
      console.log("Logout Done")
      localStorage.removeItem('userId');
      localStorage.removeItem('token');
      localStorage.removeItem('profilePicture');
      this.router.navigate(['/login']);
    })
  }

  constructor(private router: Router, private httpService: HttpService) {
    this.router.events
      .pipe(filter((events) => events instanceof NavigationEnd))
      .subscribe((event: any) => {
        const navigationEndEvent = event as NavigationEnd;
        this.showLayout = navigationEndEvent.urlAfterRedirects !== '/login' &&
          navigationEndEvent.urlAfterRedirects !== '/signup';
        this.httpService.getProfilePicture().subscribe((data) => {
          if (data.size === 0) {
            this.src = './assets/profile.png'
          } else {
            localStorage.setItem('profilePicture', URL.createObjectURL(data))
            this.src = URL.createObjectURL(data)
          }
        })
      })
  }
}
