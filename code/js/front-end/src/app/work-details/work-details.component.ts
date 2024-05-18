import {Component, inject} from '@angular/core';
import {ActivatedRoute, Router, RouterLink} from '@angular/router';
import {Work} from "../classes";
import {HttpService} from '../http.service';
import {HttpClientModule} from "@angular/common/http";
import {MatTab, MatTabGroup} from "@angular/material/tabs";
import {NgClass, NgForOf} from "@angular/common";
import {MatCardModule} from "@angular/material/card";

@Component({
  selector: 'app-work-details',
  standalone: true,
  imports: [
    HttpClientModule,
    MatTabGroup,
    MatTab,
    NgForOf,
    NgClass,
    MatCardModule,
    RouterLink
  ],
  providers: [HttpService],
  templateUrl: './work-details.component.html',
  styleUrl: './work-details.component.css'
})
export class WorkDetailsComponent {
  route: ActivatedRoute = inject(ActivatedRoute);
  httpService = inject(HttpService);
  work : Work | undefined;
  constructor(private router: Router) {
    const workListingId =  String(this.route.snapshot.params['id']);
    this.httpService.getWorkById(workListingId).subscribe((work: Work) => {
      this.work = work;
    });
  }

  onCardClick(id: number) {
    this.router.navigate([`/profile/${id}`])
  }
}
