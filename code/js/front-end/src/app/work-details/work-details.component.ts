import {Component, inject} from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import {WorkListing} from "../work-listings/worklisting";
import {WorkService} from "../work/work.service";

@Component({
  selector: 'app-work-details',
  standalone: true,
  imports: [],
  templateUrl: './work-details.component.html',
  styleUrl: './work-details.component.css'
})
export class WorkDetailsComponent {
  route: ActivatedRoute = inject(ActivatedRoute);
  workService = inject(WorkService);
  workListing : WorkListing | undefined;
  constructor() {
    const workListingId =  Number(this.route.snapshot.params['id']);
    this.workListing = this.workService.getWorkListingById(workListingId);

  }
}
