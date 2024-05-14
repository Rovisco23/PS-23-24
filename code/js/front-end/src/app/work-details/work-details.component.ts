import {Component, inject} from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import {Work} from "../work-listings/worklisting";
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
  constructor() {
    const workListingId =  String(this.route.snapshot.params['id']);
    console.log("Dentro do Construtor")
    this.workService.getWorkById(workListingId).subscribe((work: Work) => {
      this.work = work;
    });
  }
}
