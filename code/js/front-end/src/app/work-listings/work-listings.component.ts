import { Component, Input } from '@angular/core';
import { Classes } from '../classes';
import {RouterLink} from "@angular/router";

@Component({
  selector: 'app-work-listings',
  standalone: true,
  imports: [
    RouterLink
  ],
  templateUrl: './work-listings.component.html',
  styleUrl: './work-listings.component.css'
})
export class WorkListingsComponent {
  @Input() workListing!: Classes;
}
