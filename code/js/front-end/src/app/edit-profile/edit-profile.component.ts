import {Component, inject} from '@angular/core';
import {HttpService} from "../http.service";
import {Router} from "@angular/router";
import {User} from "../classes";
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import {MatButton} from "@angular/material/button";
import {NgIf} from "@angular/common";

@Component({
  selector: 'app-edit-profile',
  standalone: true,
  imports: [
    FormsModule,
    MatButton,
    ReactiveFormsModule,
    NgIf
  ],
  templateUrl: './edit-profile.component.html',
  styleUrl: './edit-profile.component.css'
})
export class EditProfileComponent {
  user: User | undefined

  httpService = inject(HttpService);

  constructor(private router: Router) {
    const uId =  localStorage.getItem('userId');
    this.httpService.getProfile(uId!!).subscribe((user: User) => {
      this.user = user;
    });
  }
  edit(){
    if (this.user) {
      this.httpService.editProfile(this.user).subscribe(() => {
        console.log("Edit Profile Finished");
        this.router.navigate(['/profile'], { queryParams: { edit: true } }); // Reset the 'edit' query parameter
      });
    } else {
      console.error('User data is not available');
    }
  }
}
