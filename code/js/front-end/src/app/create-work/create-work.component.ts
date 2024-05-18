import {Component, inject} from '@angular/core';
import {freguesias, concelhos} from '../utils/utils';
import {HttpService} from "../utils/http.service";
import {InputWork} from "../utils/classes";
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

  work: InputWork | undefined = undefined;

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
    if (this.work) {
      this.httpService.createWork(this.work).subscribe(() => {
        console.log("Sign Up Finished");
        this.router.navigate(['/login']);
      })
    } else {
      this.router.navigate(['/login']);
    }
  }
}
