import {Component, inject} from '@angular/core';
import {TopbarComponent} from "../topbar/topbar.component";
import {SidebarComponent} from "../sidebar/sidebar.component";
import {WorkListingsComponent} from "../work-listings/work-listings.component";
import {CommonModule} from "@angular/common";
import {WorkService} from './work.service';
import {WorkListing} from "../work-listings/worklisting";

@Component({
  selector: 'app-work',
  standalone: true,
  imports: [
    CommonModule,
    WorkListingsComponent,
    TopbarComponent,
    SidebarComponent
  ],
  templateUrl: './work.component.html',
  styleUrl: './work.component.css'
})
export class WorkComponent {
  workListingsList: WorkListing[] = [];
  filteredWorkList: WorkListing[] = [];
  workService: WorkService = inject(WorkService);

  constructor() {
    this.workListingsList = this.workService.getWorkListings();
    this.filteredWorkList = this.workListingsList;
  }

  filterResults(text: string) {
    if (!text) {
      this.filteredWorkList = this.workListingsList;
      return;
    }

    this.filteredWorkList = this.workListingsList.filter(
      workListing => workListing?.city.toLowerCase().includes(text.toLowerCase())
    );
  }
}
