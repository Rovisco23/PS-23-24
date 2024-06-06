import {Component, inject} from '@angular/core';
import {ActivatedRoute, Router} from "@angular/router";
import {HttpService} from "../utils/http.service";
import {LogEntry} from "../utils/classes";
import {NgIf, Location} from "@angular/common";
import {MatIcon} from "@angular/material/icon";
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import {MatButton, MatFabButton} from "@angular/material/button";
import {
  MatCell,
  MatCellDef,
  MatColumnDef,
  MatHeaderCell, MatHeaderCellDef,
  MatHeaderRow,
  MatHeaderRowDef,
  MatRow, MatRowDef, MatTable
} from "@angular/material/table";
import {MatFormField, MatLabel} from "@angular/material/form-field";
import {MatInput} from "@angular/material/input";

@Component({
  selector: 'app-log-entry-details',
  standalone: true,
  imports: [
    NgIf,
    MatIcon,
    FormsModule,
    MatButton,
    MatCell,
    MatCellDef,
    MatColumnDef,
    MatFabButton,
    MatFormField,
    MatHeaderCell,
    MatHeaderRow,
    MatHeaderRowDef,
    MatInput,
    MatLabel,
    MatRow,
    MatRowDef,
    MatTable,
    ReactiveFormsModule,
    MatHeaderCellDef
  ],
  templateUrl: './log-entry-details.component.html',
  styleUrl: './log-entry-details.component.css'
})
export class LogEntryDetailsComponent {
  route: ActivatedRoute = inject(ActivatedRoute);
  httpService = inject(HttpService);
  log: LogEntry | undefined
  logId: string = ''
  files: string | undefined

  constructor(private router: Router, private location: Location) {
    this.logId = String(this.route.snapshot.params['id']);
    this.httpService.getLogById(this.logId).subscribe((log: LogEntry) => {
      this.log = log;
      this.httpService.getFiles(this.logId, this.log.workId).subscribe((data) => {
        this.files = URL.createObjectURL(data)
      })
    })
  }

  isEditable() {
    return this.log!!.editable
  }

  editCall() {
    this.router.navigate([`/edit-log/${this.logId}`]);
  }

  downloadFiles() {
    if (this.files) {
      const link = document.createElement('a');
      link.href = this.files;
      link.download = 'files.zip';
      link.click();
    }
  }

  onBackCall() {
    this.location.back()
  }

}
