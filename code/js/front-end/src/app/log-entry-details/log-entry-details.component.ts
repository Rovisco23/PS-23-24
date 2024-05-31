import {Component, inject} from '@angular/core';
import {ActivatedRoute, Router} from "@angular/router";
import {HttpService} from "../utils/http.service";
import {PreviousUrlService} from "../previous-url/previous-url.component";
import {LogEntry} from "../utils/classes";
import {NgIf} from "@angular/common";
import {MatIcon} from "@angular/material/icon";

@Component({
  selector: 'app-log-entry-details',
  standalone: true,
  imports: [
    NgIf,
    MatIcon
  ],
  templateUrl: './log-entry-details.component.html',
  styleUrl: './log-entry-details.component.css'
})
export class LogEntryDetailsComponent {
  route: ActivatedRoute = inject(ActivatedRoute);
  httpService = inject(HttpService);
  log : LogEntry | undefined
  logId: string = ''

  constructor(private router: Router, private previousUrl: PreviousUrlService) {
    this.logId =  String(this.route.snapshot.params['id']);
    this.httpService.getLogById(this.logId).subscribe((log: LogEntry) => {
      this.log = log;
    });
  }

  isEditable() {
    return this.log?.state === 'EDITÁVEL'
  }

  editCall() {
    this.router.navigate([`/edit-log/${this.logId}`]);
  }

  onBackCall() {
    const prevUrl = this.previousUrl.getPreviousUrl()
    this.router.navigate([prevUrl ?? '/work']);
  }

}
