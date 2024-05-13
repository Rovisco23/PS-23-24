import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';
import { WorkListing } from './worklisting';
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
  @Input() workListing!: WorkListing;
}
