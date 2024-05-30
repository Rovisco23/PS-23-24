import {Component, inject} from '@angular/core';
import {WorkListingsComponent} from "../work-listings/work-listings.component";
import {CommonModule} from "@angular/common";
import {HttpService} from '../utils/http.service';
import {Classes} from "../utils/classes";
import {HttpClientModule} from "@angular/common/http";
import {Router, RouterLink} from "@angular/router";
import {MatIcon} from "@angular/material/icon";
import {MatFabButton} from "@angular/material/button";
import {FormsModule} from "@angular/forms";

@Component({
  selector: 'app-work',
  standalone: true,
  imports: [
    HttpClientModule,
    CommonModule,
    WorkListingsComponent,
    RouterLink,
    MatIcon,
    MatFabButton,
    FormsModule
  ],
  providers: [HttpService],
  templateUrl: './work.component.html',
  styleUrl: './work.component.css'
})
export class WorkComponent {
  workListingsList: Classes[] = [];
  filteredWorkList: Classes[] = [];
  inputValue: string = '';
  httpService: HttpService = inject(HttpService);

  constructor(private router: Router) {
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

  createWork() {
    this.router.navigate(['/create-work']);
  }
}
