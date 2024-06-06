import {Component, inject} from '@angular/core';
import {HttpService} from "../utils/http.service";
import {User} from "../utils/classes";
import {ActivatedRoute, Router, RouterLink, RouterLinkActive, RouterOutlet} from "@angular/router";
import {NgIf, Location} from "@angular/common";
import {MatIcon} from "@angular/material/icon";
import {catchError, throwError} from "rxjs";
import {ErrorHandler} from "../utils/errorHandle";

@Component({
  selector: 'app-profile',
  standalone: true,
  imports: [
    RouterLink,
    RouterLinkActive,
    RouterOutlet,
    NgIf,
    MatIcon
  ],
  templateUrl: './profile.component.html',
  styleUrl: './profile.component.css'
})
export class ProfileComponent {
  httpService = inject(HttpService);
  user: User | undefined;
  edit: boolean = false;
  profileSrc = '';

  constructor(private router: Router, private route: ActivatedRoute, private location: Location, private errorHandle: ErrorHandler) {
    this.loadUser();
    this.route.queryParams.subscribe(params => {
      if (params['edit'] === 'true') {
        this.edit = false;
        this.loadUser()
      }
    });
    this.httpService.getProfilePicture().pipe(
      catchError(error => {
        this.errorHandle.handleError(error);
        return throwError(error);
      })
    ).subscribe((data) => {
      if (data.size === 0) {
        this.profileSrc = './assets/profile.png'
      } else {
        localStorage.setItem('profilePicture', URL.createObjectURL(data))
        this.profileSrc = URL.createObjectURL(data)
      }
    })
  }

  loadUser() {
    const uId = String(this.route.snapshot.params['id'] ?? localStorage.getItem('userId'));
    this.httpService.getProfile(uId).pipe(
      catchError(error => {
        this.errorHandle.handleError(error);
        return throwError(error);
      })
    ).subscribe((user: User) => {
      this.user = user;
      if (user.role === 'ADMIN') {
        this.user.role = 'Admin';
      } else if (user.role === 'OPERÁRIO') {
        this.user.role = 'Operário';
      } else if (user.role === 'CÂMARA') {
        this.user.role = 'Câmara Municipal';
      }
    });
  }

  isOwner() {
    return String(this.user?.id) === localStorage.getItem('userId');
  }

  editCall() {
    this.edit = true;
    this.router.navigate(['/edit-profile']);
  }

  onBackCall() {
    this.location.back()
  }
}
