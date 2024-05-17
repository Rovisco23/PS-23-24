import {Component, inject} from '@angular/core';
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import {Router, RouterLink, RouterLinkActive} from "@angular/router";
import {HttpService} from "../http.service";
import {HttpClientModule} from "@angular/common/http";
import {MatButton} from "@angular/material/button";

@Component({
  selector: 'app-sign-up',
  standalone: true,
  imports: [
    HttpClientModule,
    FormsModule,
    ReactiveFormsModule,
    MatButton,
    RouterLink,
    RouterLinkActive
  ],
  templateUrl: './sign-up.component.html',
  styleUrl: './sign-up.component.css',
  providers: [HttpService]
})

export class SignUpComponent {
  email: string = '';
  username: string = '';
  password: string = '';
  firstName: string = '';
  lastName: string = '';
  nif: number = 0;
  phone: string = '';
  parish: string = '';
  county: string = '';
  role: string = 'OPERÁRIO';

  isChecked: boolean = false;

  httpService = inject(HttpService);

  constructor(private router: Router) {
  }

  changeRole():void {
    this.role = this.isChecked ? 'CÂMARA' : 'OPERÁRIO';
  }

  signUp(): void {
    this.httpService.signup(this.email, this.username, this.password, this.firstName, this.lastName, this.nif,
      this.phone, this.parish, this.county, this.role).subscribe(() => {
      console.log("Sign Up Finished");
      this.router.navigate(['/login']);
    })
  }

}
