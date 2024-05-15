import {Component, inject} from '@angular/core';
import {WorkListingsComponent} from "../work-listings/work-listings.component";
import {CommonModule} from "@angular/common";
import {WorkService} from './work.service';
import {WorkListing} from "../work-listings/worklisting";
import {HttpClientModule} from "@angular/common/http";

@Component({
  selector: 'app-work',
  standalone: true,
  imports: [
    HttpClientModule,
    CommonModule,
    WorkListingsComponent
  ],
  providers: [WorkService],
  templateUrl: './work.component.html',
  styleUrl: './work.component.css'
})
export class WorkComponent {
  workListingsList: WorkListing[] = [];
  filteredWorkList: WorkListing[] = [];
  workService: WorkService = inject(WorkService);

  constructor() {
    this.workService.getWorkListings().subscribe(res => {
      this.workListingsList = res;
      this.filteredWorkList = this.workListingsList.slice(0, 10);
    });
  }

  filterResults(text: string) {
    if (!text) {
      this.filteredWorkList = this.workListingsList;
      return;
    }

    this.filteredWorkList = this.workListingsList.filter(
      workListing => workListing?.name.toLowerCase().includes(text.toLowerCase())
    );
  }
}
