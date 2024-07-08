import {Component, inject} from '@angular/core';
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import {RouterLink, RouterLinkActive} from "@angular/router";
import {HttpService} from "../utils/http.service";
import {HttpClientModule} from "@angular/common/http";
import {MatButton} from "@angular/material/button";
import {NgForOf} from "@angular/common";
import {concelhos, freguesias} from "../utils/utils";
import {catchError, throwError} from "rxjs";
import {ErrorHandler} from "../utils/errorHandle";
import {NavigationService} from "../utils/navService";
import {SnackBar} from "../utils/snackBarComponent";

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
  district: string = '';
  role: string = 'OPERÁRIO';
  association_name: string = '';
  association_num: string = '';


  isChecked: boolean = false;

  districts: string[] = [];
  counties: string[] = [];
  parishes: string[] = [];

  httpService = inject(HttpService);

  constructor(
    private errorHandle: ErrorHandler,
    private navService: NavigationService,
    private snackBar: SnackBar
  ) {
    concelhos.forEach((value, key) => {
      value.forEach((v: string) => this.counties.push(v));
      this.districts.push(key);
    })
    freguesias.forEach((value) => {
      value.forEach((x: string) => this.parishes.push(x));
    })
  }

  changeRole(): void {
    this.role = this.isChecked ? 'CÂMARA' : 'OPERÁRIO';
  }

  updateLocation(change: boolean) {
    const selectedParish = this.parish;
    const selectedCounty = this.county;
    if (!change) {
      const cList: string[] = [];
      const dList: string[] = [];
      for (const c of freguesias.keys()) {
        const pList = freguesias.get(c);
        if (pList.includes(selectedParish)) {
          cList.push(c);
        }
      }
      for (const d of concelhos.keys()) {
        const conList = concelhos.get(d);
        for (const c of cList) {
          if (conList.includes(c)) {
            dList.push(d);
          }
        }
      }
      this.counties = cList;
      this.districts = dList;
      this.county = cList[0];
    }
    if (change) {
      const dList: string[] = [];
      for (const d of concelhos.keys()) {
        const cList = concelhos.get(d);
        if (cList.includes(selectedCounty)) {
          dList.push(d);
        }
      }
      this.districts = dList;
    }
    this.district = this.districts[0];
  }

  signUp(): void {
    this.httpService.signup(this.email, this.username, this.password, this.firstName, this.lastName, this.nif,
      this.phone, this.parish, this.county, this.district, this.role, this.association_name, Number(this.association_num)).pipe(
      catchError(error => {
        this.errorHandle.handleError(error);
        return throwError(error);
      })
    ).subscribe(() => {
      console.log("Sign Up Finished");
      this.navService.navLogin()
      this.snackBar.openSnackBar('Conta criada com sucesso.');

    })
  }

  onLoginClick(): void {
    this.navService.navLogin()
  }
}
