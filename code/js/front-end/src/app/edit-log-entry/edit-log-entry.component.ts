import {Component, inject} from '@angular/core';
import {LogEditableEntry, LogEntry} from "../utils/classes";
import {HttpService} from "../utils/http.service";
import {ActivatedRoute, Router} from "@angular/router";
import {PreviousUrlService} from "../previous-url/previous-url.component";
import {MatIcon} from "@angular/material/icon";
import {FormsModule} from "@angular/forms";
import {MatButton} from "@angular/material/button";
import {NgIf} from "@angular/common";
import {MatFormField, MatInput, MatLabel} from "@angular/material/input";

@Component({
  selector: 'app-edit-log-entry',
  standalone: true,
  imports: [
    MatIcon,
    FormsModule,
    MatButton,
    NgIf,
    MatInput,
    MatFormField,
    MatLabel
  ],
  templateUrl: './edit-log-entry.component.html',
  styleUrl: './edit-log-entry.component.css'
})
export class EditLogEntryComponent {
  route: ActivatedRoute = inject(ActivatedRoute);
  httpService = inject(HttpService);
  form: FormData = new FormData()
  log: LogEditableEntry = {workId: '', title: '', content: ''}
  logId: string = ''

  constructor(private router: Router, private previousUrl: PreviousUrlService) {
    this.logId = String(this.route.snapshot.params['id']);
    this.httpService.getLogById(this.logId).subscribe((log: LogEntry) => {
      this.log.title = log.title
      this.log.content = log.content
      this.log.workId = log.workId
    });
  }

  edit() {
    if (this.log) {
      this.form.append("log", new Blob([JSON.stringify({
        title: this.log.title,
        description: this.log.content,
        workId: this.log.workId
      })], {type: 'application/json'}))
      this.httpService.editLog(this.form, this.logId).subscribe(() => {
        this.router.navigate([`/log-entry/${this.logId}`])
      });
    }
  }

  onBackCall() {
    const prevUrl = this.previousUrl.getPreviousUrl()
    this.router.navigate([prevUrl ?? '/work']);
  }
}
