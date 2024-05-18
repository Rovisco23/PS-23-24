import {Component, inject} from '@angular/core';
import {WorkListingsComponent} from "../work-listings/work-listings.component";
import {CommonModule} from "@angular/common";
import {HttpService} from '../http.service';
import {Classes} from "../classes";
import {HttpClientModule} from "@angular/common/http";

@Component({
  selector: 'app-work',
  standalone: true,
  imports: [
    HttpClientModule,
    CommonModule,
    WorkListingsComponent
  ],
  providers: [HttpService],
  templateUrl: './work.component.html',
  styleUrl: './work.component.css'
})
export class WorkComponent {
  workListingsList: Classes[] = [];
  filteredWorkList: Classes[] = [];
  httpService: HttpService = inject(HttpService);

  constructor() {
    this.httpService.getWorkListings().subscribe(res => {
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
