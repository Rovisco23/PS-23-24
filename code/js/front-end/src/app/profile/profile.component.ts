import {Component, inject} from '@angular/core';
import {HttpService} from "../http.service";
import {User} from "../classes";
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
    this.httpService.getProfile().subscribe((user: User) => {
      this.user = user;
    });
  }

  editCall() {
    this.edit = true;
    this.router.navigate(['/profile/edit']);
  }
}
