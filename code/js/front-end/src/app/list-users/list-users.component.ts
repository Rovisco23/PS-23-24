import {Component, inject} from '@angular/core';
import {MatIcon} from "@angular/material/icon";
import {Router} from "@angular/router";
import {Location, NgForOf} from "@angular/common";
import {User} from "../utils/classes";
import {HttpService} from "../utils/http.service";
import {MatDivider} from "@angular/material/divider";
import {MatIconButton} from "@angular/material/button";
import {MatList, MatListItem, MatListItemLine, MatListItemMeta, MatListItemTitle} from "@angular/material/list";
import {FormsModule} from "@angular/forms";

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

  constructor(private router: Router, private location: Location) {
    this.httpService.getAllUsers().subscribe((res) => {
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
    this.location.back()
  }

  onUserClick(id: String) {
    this.router.navigate(['/profile', id])
  }
}
