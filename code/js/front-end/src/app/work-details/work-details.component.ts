import {Component, inject} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {Member, Work} from "../work-listings/worklisting";
import {WorkService} from "../work/work.service";
import {HttpClientModule} from "@angular/common/http";

@Component({
  selector: 'app-work-details',
  standalone: true,
  imports: [
    HttpClientModule,
  ],
  providers: [WorkService],
  templateUrl: './work-details.component.html',
  styleUrl: './work-details.component.css'
})
export class WorkDetailsComponent {
  route: ActivatedRoute = inject(ActivatedRoute);
  workService = inject(WorkService);
  work : Work | undefined;
  constructor(private router: Router) {
    const workListingId =  String(this.route.snapshot.params['id']);
    this.workService.getWorkById(workListingId).subscribe((work: Work) => {
      this.work = work;
    });
  }

  navigateToWorkMembers(id: string, members: Member[]) {
    const navigationExtras = {
      state: {
        data: members
      }
    };
    this.router.navigate(['/work-members', id], navigationExtras);
  }
}
