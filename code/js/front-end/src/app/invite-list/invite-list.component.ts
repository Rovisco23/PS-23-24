import {Component, inject} from '@angular/core';
import {HttpService} from "../utils/http.service";
import {Router} from "@angular/router";
import {InviteSimplified, Role} from "../utils/classes";
import {DatePipe, NgForOf} from "@angular/common";
import {MatDivider} from "@angular/material/divider";
import {MatFabButton} from "@angular/material/button";
import {MatIcon} from "@angular/material/icon";
import {MatList, MatListItem, MatListItemLine, MatListItemTitle} from "@angular/material/list";

@Component({
  selector: 'app-invite-list',
  standalone: true,
  imports: [
    DatePipe,
    MatDivider,
    MatFabButton,
    MatIcon,
    MatList,
    MatListItem,
    MatListItemLine,
    MatListItemTitle,
    NgForOf
  ],
  templateUrl: './invite-list.component.html',
  styleUrl: './invite-list.component.css'
})
export class InviteListComponent {

  invites: InviteSimplified[] = [];

  filteredInvites: InviteSimplified[] = [];

  httpService: HttpService = inject(HttpService);

  constructor(private router: Router) {
    this.httpService.getInviteList().subscribe(res => {
      this.invites = this.composeInvites(res);
      this.filteredInvites = this.invites;
    });
  }

  filterInvites(text: string) {
    if (!text) {
      this.filteredInvites = this.invites;
      return;
    }
    this.filteredInvites = this.invites.filter(
      entry => entry.workTitle.toLowerCase().includes(text.toLowerCase())
    );
  }

  composeInvites(invites: InviteSimplified[]): InviteSimplified[] {
    return invites.map(invite => {
      invite.role = Role.composeRole(invite.role);
      return invite;
    });
  }

  onInviteClick(id: string) {
    this.router.navigate([`/invites/${id}`]);
  }

  onBackCall(){
    console.log("Back Not Implemented Yet")
  }
}
