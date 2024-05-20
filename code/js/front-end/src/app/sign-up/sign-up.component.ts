import {Component, inject} from '@angular/core';
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import {Router, RouterLink, RouterLinkActive} from "@angular/router";
import {HttpService} from "../utils/http.service";
import {HttpClientModule} from "@angular/common/http";
import {MatButton} from "@angular/material/button";
import {NgForOf} from "@angular/common";
import {concelhos, freguesias} from "../utils/utils";

@Component({
  selector: 'app-sign-up',
  standalone: true,
  imports: [
    HttpClientModule,
    FormsModule,
    ReactiveFormsModule,
    MatButton,
    RouterLink,
    RouterLinkActive,
    NgForOf
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

  counties: string[] = [];
  parishes: string[] = [];

  httpService = inject(HttpService);

  constructor(private router: Router) {
    concelhos.forEach((value) => {
      value.forEach((v: string) => this.counties.push(v));
    })
    freguesias.forEach((value) => {
      value.forEach((x: string) => this.parishes.push(x));
    })
  }

  changeRole(): void {
    this.role = this.isChecked ? 'CÂMARA' : 'OPERÁRIO';
  }

  updateLocation() {
    const selectedParish = this.parish;
    const cList: string[] = [];
    for (const c of freguesias.keys()) {
      const pList = freguesias.get(c);
      if (pList.includes(selectedParish)) {
        cList.push(c);
      }
    }
    this.counties = cList;
    this.county = this.counties[0];
  }

  signUp(): void {
    this.httpService.signup(this.email, this.username, this.password, this.firstName, this.lastName, this.nif,
      this.phone, this.parish, this.county, this.role).subscribe(() => {
      console.log("Sign Up Finished");
      this.router.navigate(['/login']);
    })
  }

}
