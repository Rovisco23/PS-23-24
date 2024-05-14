import {Component, inject} from '@angular/core';
import {ActivatedRoute, Router, RouterLink} from "@angular/router";
import {NgForOf} from "@angular/common";
import {WorkListingsComponent} from "../work-listings/work-listings.component";
import {WorkMembersListingComponent} from "../work-members-listing/work-members-listing.component";
import {Member} from "../work-listings/worklisting";

@Component({
  selector: 'app-work-members',
  standalone: true,
  imports: [
    RouterLink,
    NgForOf,
    WorkMembersListingComponent
  ],
  templateUrl: './work-members.component.html',
  styleUrl: './work-members.component.css'
})
export class WorkMembersComponent {
  route: ActivatedRoute = inject(ActivatedRoute);
  workId: string = '';
  membersList: Member[] = [];
  constructor(private router: Router) {
    this.workId = String(this.route.snapshot.params['id']);
    this.membersList = this.router.getCurrentNavigation()?.extras.state?.['data'];
  }
}
