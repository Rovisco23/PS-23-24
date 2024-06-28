import {Component, ErrorHandler, inject} from '@angular/core';
import {MatDivider} from "@angular/material/divider";
import {MatIcon} from "@angular/material/icon";
import {MatList, MatListItem, MatListItemLine, MatListItemTitle} from "@angular/material/list";
import {NgForOf} from "@angular/common";
import {Verification} from "../utils/classes";
import {FormsModule} from "@angular/forms";
import {catchError, throwError} from "rxjs";
import {HttpService} from "../utils/http.service";
import {MatIconButton} from "@angular/material/button";
import {NavigationService} from "../utils/navService";

@Component({
  selector: 'app-work-verifications',
  standalone: true,
  imports: [
    MatDivider,
    MatIcon,
    MatList,
    MatListItem,
    MatListItemLine,
    MatListItemTitle,
    NgForOf,
    FormsModule,
    MatIconButton
  ],
  templateUrl: './work-verifications.component.html',
  styleUrl: './work-verifications.component.css'
})
export class WorkVerificationsComponent {

  verifications: Verification[] = [];
  filteredVerifications: Verification[] = [];

  httpService: HttpService = inject(HttpService);

  inputValue: string = '';

  constructor(private navService: NavigationService, private errorHandle: ErrorHandler) {
    this.httpService.getWorksPending().pipe(
      catchError(error => {
        this.errorHandle.handleError(error);
        return throwError(error);
      })
    ).subscribe(res => {
      this.verifications = res;
      this.filteredVerifications = res;
    });
  }

  filterVerifications(text: string) {
    if (!text) {
      this.filteredVerifications = this.verifications;
      return;
    }
    this.filteredVerifications = this.verifications.filter(
      entry => entry.name.toLowerCase().includes(text.toLowerCase())
    );
  }

  onAccept(id: string) {
    this.httpService.answerPendingWork(id, true).pipe().subscribe(() => {
      this.navService.navWork();
    })
  }

  onDecline(id: string) {
    this.httpService.answerPendingWork(id, false).pipe().subscribe(() => {
      this.navService.navWork();
    })
  }

  onBackCall() {
    this.navService.back();
  }
}
