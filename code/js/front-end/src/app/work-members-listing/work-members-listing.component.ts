import {Component, Input} from '@angular/core';
import {Member} from "../work-listings/worklisting";

@Component({
  selector: 'app-work-members-listing',
  standalone: true,
  imports: [],
  templateUrl: './work-members-listing.component.html',
  styleUrl: './work-members-listing.component.css'
})
export class WorkMembersListingComponent {
  @Input() member!: Member;
}
