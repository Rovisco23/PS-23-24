import {Component, inject} from '@angular/core';
import {HttpService} from "../utils/http.service";
import {User} from "../utils/classes";
import {ActivatedRoute, Router, RouterLink, RouterLinkActive, RouterOutlet} from "@angular/router";
import {NgIf} from "@angular/common";

@Component({
  selector: 'app-profile',
  standalone: true,
  imports: [
    RouterLink,
    RouterLinkActive,
    RouterOutlet,
    NgIf
  ],
  templateUrl: './profile.component.html',
  styleUrl: './profile.component.css'
})
export class ProfileComponent {
  httpService = inject(HttpService);
  user: User | undefined;
  edit: boolean = false;

  constructor(private router: Router, private route: ActivatedRoute) {
    this.loadUser(); // Initial load of user data

    // Check if the 'edit' query parameter is present
    this.route.queryParams.subscribe(params => {
      if (params['edit'] === 'true') {
        this.edit = false;
        this.loadUser()
      }
    });
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

  protected readonly localStorage = localStorage;
}
