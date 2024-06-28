import {Component, inject} from '@angular/core';
import {MatDivider} from "@angular/material/divider";
import {MatIcon} from "@angular/material/icon";
import {
  MatList,
  MatListItem,
  MatListItemLine,
  MatListItemMeta,
  MatListItemTitle,
  MatListSubheaderCssMatStyler
} from "@angular/material/list";
import {NgForOf} from "@angular/common";
import {HttpService} from "../utils/http.service";
import {Pending} from "../utils/classes";
import {MatButton, MatIconButton} from "@angular/material/button";
import {catchError, throwError} from "rxjs";
import {ErrorHandler} from "../utils/errorHandle";
import {NavigationService} from "../utils/navService";

@Component({
  selector: 'app-pending-users',
  standalone: true,
  imports: [
    MatDivider,
    MatIcon,
    MatList,
    MatListItem,
    MatListItemLine,
    MatListItemTitle,
    NgForOf,
    MatListItemMeta,
    MatButton,
    MatIconButton,
    MatListSubheaderCssMatStyler
  ],
  templateUrl: './pending-users.component.html',
  styleUrl: './pending-users.component.css'
})
export class PendingUsersComponent {

  httpService: HttpService = inject(HttpService);

  pendingList: Pending[] = [];

  filteredPendingList: Pending[] = [];

  constructor(private errorHandle: ErrorHandler, private navService: NavigationService) {
    this.httpService.getPendingUsers().pipe(
      catchError(error => {
        this.errorHandle.handleError(error);
        return throwError(error);
      })
    ).subscribe((res) => {
      this.pendingList = res;
      this.filteredPendingList = this.pendingList;
    });
  }

  filterPending(text: string) {
    if (!text) {
      this.filteredPendingList = this.pendingList;
      return;
    }
    this.filteredPendingList = this.pendingList.filter(
      entry => entry.email.toLowerCase().includes(text.toLowerCase())
    );
  }

  onItemClick(id: number) {
    this.navService.navMemberProfile(id);
  }

  onAccept(id: number) {
    this.httpService.answerPending(id, true).pipe(
      catchError(error => {
        this.errorHandle.handleError(error);
        return throwError(error);
      })
    ).subscribe(() => {
      this.filteredPendingList = this.filteredPendingList.filter(item => item.id !== id);
      console.log("Accepted");
    });
  }

  onDecline(id: number) {
    this.httpService.answerPending(id, false).pipe(
      catchError(error => {
        this.errorHandle.handleError(error);
        return throwError(error);
      })
    ).subscribe(() => {
      this.filteredPendingList = this.filteredPendingList.filter(item => item.id !== id);
      console.log("Declined");
    });
  }

  onBackCall() {
    this.navService.back()
  }
}
