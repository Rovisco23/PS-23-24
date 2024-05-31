import {Component, inject} from '@angular/core';
import {HttpService} from "../utils/http.service";
import {Router} from "@angular/router";
import {User} from "../utils/classes";
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import {MatButton} from "@angular/material/button";
import {NgForOf, NgIf} from "@angular/common";
import {MatIcon} from "@angular/material/icon";
import {MatFormField, MatLabel} from "@angular/material/form-field";
import {MatInput} from "@angular/material/input";
import {concelhos, freguesias} from "../utils/utils";

@Component({
  selector: 'app-edit-profile',
  standalone: true,
  imports: [
    FormsModule,
    MatButton,
    ReactiveFormsModule,
    NgIf,
    MatIcon,
    MatFormField,
    MatInput,
    MatLabel,
    NgForOf
  ],
  templateUrl: './edit-profile.component.html',
  styleUrl: './edit-profile.component.css'
})
export class EditProfileComponent {
  user: User | undefined
  editSrc = ''
  form: FormData = new FormData()
  newFile: any = ''
  newSrc: string | undefined = undefined

  counties: string[] = [];
  parishes: string[] = [];

  httpService = inject(HttpService);

  constructor(private router: Router) {
    const uId =  localStorage.getItem('userId');
    this.httpService.getProfile(uId!!).subscribe((user: User) => {
      this.user = user;
    });
    this.httpService.getProfilePicture().subscribe((data) => {
      if (data.size === 0) {
        this.editSrc = './assets/profile.png'
      } else {
        localStorage.setItem('profilePicture', URL.createObjectURL(data))
        this.editSrc = URL.createObjectURL(data)
      }
    })
    concelhos.forEach((value) => {
      value.forEach((v: string) => this.counties.push(v));
    })
    freguesias.forEach((value) => {
      value.forEach((x: string) => this.parishes.push(x));
    })
  }

  updateLocation() {
    if(this.user){
      const selectedParish = this.user.location.parish;
      const cList: string[] = [];
      for (const c of freguesias.keys()) {
        const pList = freguesias.get(c);
        if (pList.includes(selectedParish)) {
          cList.push(c);
        }
      }
      this.counties = cList;
      this.user.location.county = this.counties[0];
    }
  }

  onImageChange(event: any) {
    this.newFile = event.target.files[0];
    const reader = new FileReader();

    reader.onload = () => {
      this.newSrc = reader.result as string;
    };

    reader.readAsDataURL(this.newFile);
  }

  onSubmitPicture(){
    this.form.append('file', this.newFile);
    this.httpService.changeProfilePicture(this.form).subscribe(() => {});
  }

  onRemovePicture(){
    this.httpService.changeProfilePicture(this.form).subscribe(() => {
      this.editSrc = './assets/profile.png';
    });
  }

  onSubmitEdit(){
    if (this.user) {
      this.httpService.editProfile(this.user).subscribe(() => {
        console.log("Edit Profile Finished");
        this.router.navigate(['/profile'], { queryParams: { edit: true } }); // Reset the 'edit' query parameter
      });
    } else {
      console.error('User data is not available');
    }
  }

  onBackCall(){
    this.router.navigate(['/profile']);
  }
}
