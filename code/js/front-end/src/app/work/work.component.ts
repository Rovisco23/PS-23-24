import {Component, inject} from '@angular/core';
import {WorkListingsComponent} from "../work-listings/work-listings.component";
import {CommonModule} from "@angular/common";
import {HttpService} from '../utils/http.service';
import {Classes} from "../utils/classes";
import {HttpClientModule} from "@angular/common/http";
import {Router, RouterLink} from "@angular/router";
import {MatIcon} from "@angular/material/icon";
import {MatButton, MatFabButton} from "@angular/material/button";
import {FormsModule} from "@angular/forms";
import {catchError, throwError} from "rxjs";
import {ErrorHandler} from "../utils/errorHandle";
import {MatBadge} from "@angular/material/badge";

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
    FormsModule,
    MatButton,
    MatBadge
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
  numberOfVerifications: number = 0;

  constructor(private router: Router, private errorHandle: ErrorHandler) {
    if (localStorage.getItem('token')) {
      this.httpService.getWorkListings().pipe(
        catchError(error => {
          this.errorHandle.handleError(error);
          return throwError(error);
        })
      ).subscribe(res => {
        this.workListingsList = res;
        this.filteredWorkList = this.workListingsList.slice(0, 10);
      });
      if (localStorage.getItem('role') === 'CÂMARA') {
        /*this.httpService.getNumberOfVerifications(localStorage.getItem('userId') ?? '').pipe(
          catchError(error => {
            this.errorHandle.handleError(error);
            return throwError(error);
          })
        ).subscribe(res => {
          this.numberOfVerifications = res;
        });*/
      }
    }

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

  checkCouncil(){
    return localStorage.getItem('role') === 'CÂMARA';
  }

  councilVerifications(){
    this.router.navigate(['/verifications']);
  }

  createWork() {
    this.router.navigate(['/create-work']);
  }
}
