import {Component, inject} from '@angular/core';
import {freguesias, concelhos} from '../utils/utils';
import {HttpService} from "../utils/http.service";
import {InputWork, WorkTypes} from "../utils/classes";
import {Router} from "@angular/router";
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import {MatButton} from "@angular/material/button";
import {MatFormField, MatOption, MatSelect} from "@angular/material/select";
import {CommonModule, NgForOf, NgIf} from "@angular/common";
import {MatIcon} from "@angular/material/icon";

@Component({
  selector: 'app-create-work',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    MatButton,
    ReactiveFormsModule,
    MatSelect,
    MatOption,
    NgForOf,
    NgIf,
    MatFormField,
    MatIcon
  ],
  templateUrl: './create-work.component.html',
  styleUrl: './create-work.component.css'
})
export class CreateWorkComponent {

  work: InputWork;
  types = Object.values(WorkTypes);

  httpService = inject(HttpService)

  counties: string[] = [];
  parishes: string[] = [];
  districts: string[] = [];

  constructor(private router: Router) {
    this.work = {
      name: '',
      type: '',
      description: '',
      holder: '',
      director: '',
      company: {
        name: '',
        num: 0
      },
      building: '',
      address: {
        location: {
          district: '',
          county: '',
          parish: ''
        },
        street: '',
        postalCode: ''
      }
    }
    concelhos.forEach((value, key) => {
      value.forEach((v: string) => this.counties.push(v));
      this.districts.push(key);
    })
    freguesias.forEach((value) => {
      value.forEach((x: string) => this.parishes.push(x));
    })
  }

  updateLocation(change: boolean) {
    const selectedParish = this.work.address.location.parish;
    const selectedCounty = this.work.address.location.county;
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
      this.work.address.location.county = cList[0];
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
    this.work.address.location.district = this.districts[0];
  }

  create() {
    this.httpService.createWork(this.work).subscribe(() => {
      console.log("Work Created!");
      this.router.navigate(['/work']);
    });
  }

  onBackCall() {
    this.router.navigate(['/work']);
  }
}
