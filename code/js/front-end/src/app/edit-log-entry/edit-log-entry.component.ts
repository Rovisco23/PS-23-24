import {Component, inject} from '@angular/core';
import {LogEditableEntry, LogEntry} from "../utils/classes";
import {HttpService} from "../utils/http.service";
import {ActivatedRoute, Router} from "@angular/router";
import {MatIcon} from "@angular/material/icon";
import {FormsModule} from "@angular/forms";
import {MatButton, MatFabButton} from "@angular/material/button";
import {NgIf, Location} from "@angular/common";
import {MatFormField, MatInput, MatLabel} from "@angular/material/input";
import {
  MatCell,
  MatCellDef,
  MatColumnDef,
  MatHeaderCell, MatHeaderCellDef,
  MatHeaderRow,
  MatHeaderRowDef,
  MatRow, MatRowDef, MatTable
} from "@angular/material/table";
import {ErrorHandler} from "../utils/errorHandle";
import {catchError, throwError} from "rxjs";

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
    MatLabel,
    MatCell,
    MatCellDef,
    MatColumnDef,
    MatFabButton,
    MatHeaderCell,
    MatHeaderRow,
    MatHeaderRowDef,
    MatRow,
    MatRowDef,
    MatTable,
    MatHeaderCellDef
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
  files: Map<string, File> = new Map<string, File>();
  displayedColumns: string[] = ['files', 'delete'];

  constructor(private router: Router, private location: Location, private errorHandle: ErrorHandler) {
    this.logId = String(this.route.snapshot.params['id']);
    this.httpService.getLogById(this.logId).pipe(
      catchError(error => {
        this.errorHandle.handleError(error);
        return throwError(error);
      })
    ).subscribe((log: LogEntry) => {
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
      this.files.forEach((file) => {
        this.form.append('files', file)
      })
      this.httpService.editLog(this.form, this.logId).pipe(
        catchError(error => {
          this.errorHandle.handleError(error);
          return throwError(error);
        })
      ).subscribe(() => {
        this.router.navigate([`/log-entry/${this.logId}`])
      });
    }
  }

  onFileUpload(event: any) {
    if (event.target.files.length > 0) {
      const file: File = event.target.files[0];
      this.files.set(file.name, file);
    }
  }

  filesArray() {
    return Array.from(this.files.entries()).map(([key, file]) => ({
      key,
      fileName: file.name,
    }));
  }

  onRemoveFile(key: string) {
    this.files.delete(key);
  }

  onBackCall() {
    this.location.back()
  }
}
