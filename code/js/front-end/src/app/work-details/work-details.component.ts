import {Component, inject} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {Work} from "../work-listings/worklisting";
import {HttpService} from '../http.service';
import {HttpClientModule} from "@angular/common/http";
import {MatTab, MatTabGroup} from "@angular/material/tabs";
import {NgClass, NgForOf} from "@angular/common";

@Component({
  selector: 'app-work-details',
  standalone: true,
  imports: [
    HttpClientModule,
    MatTabGroup,
    MatTab,
    NgForOf,
    NgClass,
  ],
  providers: [HttpService],
  templateUrl: './work-details.component.html',
  styleUrl: './work-details.component.css'
})
export class WorkDetailsComponent {
  route: ActivatedRoute = inject(ActivatedRoute);
  httpService = inject(HttpService);
  work : Work | undefined;
  constructor(private router: Router) {
    const workListingId =  String(this.route.snapshot.params['id']);
    this.httpService.getWorkById(workListingId).subscribe((work: Work) => {
      this.work = work;
    });
  }
}
