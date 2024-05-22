import {Component, inject} from '@angular/core';
import {ActivatedRoute, Router, RouterLink} from '@angular/router';
import {LogEntrySimplified, Work} from "../utils/classes";
import {HttpService} from '../utils/http.service';
import {HttpClientModule} from "@angular/common/http";
import {MatTab, MatTabGroup} from "@angular/material/tabs";
import {DatePipe, NgClass, NgForOf, NgIf} from "@angular/common";
import {MatCardModule} from "@angular/material/card";
import {MatListModule} from "@angular/material/list";
import {MatIcon} from "@angular/material/icon";
import {MatFabButton} from "@angular/material/button";

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
    MatListModule,
    RouterLink,
    DatePipe,
    MatIcon,
    MatFabButton,
    NgIf
  ],
  providers: [HttpService],
  templateUrl: './work-details.component.html',
  styleUrl: './work-details.component.css'
})
export class WorkDetailsComponent {
  route: ActivatedRoute = inject(ActivatedRoute);
  httpService = inject(HttpService);
  work : Work | undefined;
  admin : boolean | undefined;
  filteredLogList: LogEntrySimplified[] = [];
  constructor(private router: Router) {
    const workListingId =  String(this.route.snapshot.params['id']);
    this.httpService.getWorkById(workListingId).subscribe((work: Work) => {
      this.work = work;
      this.filteredLogList = work.log;
      this.admin = this.work.members.find(member => member.role === 'ADMIN')?.id === Number(localStorage.getItem('userId'));
    });
  }

  onInviteClick() {
    this.router.navigate(['/invite-members'], {state: {workId: this.work?.id}})
  }

  createNewEntry() {
    this.router.navigate([`/create-log-entry/${this.work!!.id}`]);
  }

  onLogEntryClick(id: number) {
    this.router.navigate([`/log-entry/${id}`]);
  }

  onMemberClick(id: number) {
    this.router.navigate([`/profile/${id}`])
  }

  filterResults(text: string) {
    if (!text) {
      this.filteredLogList = this.work!!.log;
      return;
    }

    this.filteredLogList = this.work!!.log.filter(
      entry => entry.content.toLowerCase().includes(text.toLowerCase())
    );
  }
}
