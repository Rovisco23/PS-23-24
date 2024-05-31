import {Component, inject} from '@angular/core';
import {HttpService} from "../utils/http.service";
import {User} from "../utils/classes";
import {ActivatedRoute, Router, RouterLink, RouterLinkActive, RouterOutlet} from "@angular/router";
import {NgIf} from "@angular/common";
import {PreviousUrlService} from "../previous-url/previous-url.component";
import {MatIcon} from "@angular/material/icon";

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

  constructor(private router: Router, private route: ActivatedRoute, private previousUrl: PreviousUrlService) {
    this.loadUser();
    this.route.queryParams.subscribe(params => {
      if (params['edit'] === 'true') {
        this.edit = false;
        this.loadUser()
      }
    });
    this.httpService.getProfilePicture().subscribe((data) => {
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
    this.httpService.getProfile(uId).subscribe((user: User) => {
      this.user = user;
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
    const prevUrl = this.previousUrl.getPreviousUrl()
    this.router.navigate([prevUrl ?? '/work']);
  }

  protected readonly localStorage = localStorage;
}
