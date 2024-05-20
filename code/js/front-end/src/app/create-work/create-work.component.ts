import {Component, inject} from '@angular/core';
import {freguesias, concelhos} from '../utils/utils';
import {HttpService} from "../utils/http.service";
import {InputWork, WorkTypes} from "../utils/classes";
import {Router} from "@angular/router";
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import {MatButton} from "@angular/material/button";
import {MatFormField, MatOption, MatSelect} from "@angular/material/select";
import {CommonModule, NgForOf, NgIf} from "@angular/common";

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
    MatFormField
  ],
  templateUrl: './create-work.component.html',
  styleUrl: './create-work.component.css'
})
export class CreateWorkComponent {

  work: InputWork;
  types = Object.values(WorkTypes);

  httpService = inject(HttpService)

  concelhos = concelhos;
  freguesias = freguesias;

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
  }

  create() {
    this.httpService.createWork(this.work).subscribe(() => {
      console.log("Work Created!");
      this.router.navigate(['/work']);
    });
  }

  check() {
    console.log("District: " + this.work?.address.location.district);
    console.log("County: " + this.work?.address.location.county);
    console.log("Parish: " + this.work?.address.location.parish);
  }
}
