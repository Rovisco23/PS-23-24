import {Component, inject} from '@angular/core';
import {MatIcon} from "@angular/material/icon";
import {NgForOf} from "@angular/common";
import {User} from "../utils/classes";
import {HttpService} from "../utils/http.service";
import {MatDivider} from "@angular/material/divider";
import {MatIconButton} from "@angular/material/button";
import {MatList, MatListItem, MatListItemLine, MatListItemMeta, MatListItemTitle} from "@angular/material/list";
import {FormsModule} from "@angular/forms";
import {catchError, throwError} from "rxjs";
import {ErrorHandler} from "../utils/errorHandle";
import {NavigationService} from "../utils/navService";

@Component({
  selector: 'app-list-users',
  standalone: true,
  imports: [
    MatIcon,
    MatDivider,
    MatIconButton,
    MatList,
    MatListItem,
    MatListItemLine,
    MatListItemMeta,
    MatListItemTitle,
    NgForOf,
    FormsModule
  ],
  templateUrl: './list-users.component.html',
  styleUrl: './list-users.component.css'
})
export class ListUsersComponent {

  httpService: HttpService = inject(HttpService)

  value: string = ''

  usersList: User[] = []

  filteredUsersList: User[] = []

  constructor(private errorHandle: ErrorHandler, private navService: NavigationService) {
    this.httpService.getAllUsers().pipe(
      catchError(error => {
        this.errorHandle.handleError(error);
        return throwError(error);
      })
    ).subscribe((res) => {
      this.usersList = res
      this.filteredUsersList = this.usersList
    });
  }

  filterUsers(text: string) {
    if (!text) {
      this.filteredUsersList = this.usersList
      return
    }
    this.filteredUsersList = this.usersList.filter(
      user => user.username.toLowerCase().includes(text.toLowerCase())
    )
  }

  onBackCall() {
    this.navService.back()
  }

  onUserClick(id: String) {
    this.navService.navMemberProfile(Number(id))
  }
}
