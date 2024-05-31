import {Component, inject} from '@angular/core';
import {MatInputModule} from '@angular/material/input';
import {MatFormFieldModule} from "@angular/material/form-field";
import {FormsModule} from '@angular/forms';
import {CommonModule, NgOptimizedImage} from "@angular/common";
import {MatList, MatListItem, MatListItemMeta, MatListItemTitle} from "@angular/material/list";
import {MatIcon} from "@angular/material/icon";
import {MatButton, MatFabButton} from "@angular/material/button";
import {ActivatedRoute, Router} from "@angular/router";
import {HttpService} from "../utils/http.service";
import {
  MatCell,
  MatCellDef,
  MatColumnDef,
  MatHeaderCell,
  MatHeaderCellDef, MatHeaderRow,
  MatHeaderRowDef, MatRow, MatRowDef,
  MatTable
} from "@angular/material/table";

@Component({
  selector: 'app-create-log-entry',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    MatFormFieldModule,
    MatInputModule,
    NgOptimizedImage,
    MatList,
    MatIcon,
    MatListItem,
    MatListItemTitle,
    MatListItemMeta,
    MatButton,
    MatCellDef,
    MatColumnDef,
    MatTable,
    MatFabButton,
    MatHeaderCellDef,
    MatCell,
    MatHeaderCell,
    MatHeaderRowDef,
    MatHeaderRow,
    MatRow,
    MatRowDef
  ],
  templateUrl: './create-log-entry.component.html',
  styleUrl: './create-log-entry.component.css'
})
export class CreateLogEntryComponent {
  route: ActivatedRoute = inject(ActivatedRoute)
  httpService = inject(HttpService)
  workId: string = String(this.route.snapshot.params['id'])
  form: FormData = new FormData()
  title: string = ''
  description: string = ''
  files: Map<string, File> = new Map<string, File>();
  displayedColumns: string[] = ['files', 'delete'];

  constructor(private router: Router) {
  }

  onSubmit() {
    this.form.append('log', new Blob([JSON.stringify({
        title: this.title,
        description: this.description,
        workId: this.workId
      })], {type: 'application/json'})
    )
    this.files.forEach((file) => {
      this.form.append('files', file)
    })
    this.httpService.createLogEntry(this.form).subscribe(() => {
      this.router.navigate([`/work/${this.workId}`])
    })
  }

  filesArray() {
    return Array.from(this.files.entries()).map(([key, file]) => ({
      key,
      email: file.name,
    }));
  }

  onFileUpload(event: any) {
    if (event.target.files.length > 0) {
      const file: File = event.target.files[0];
      this.files.set(file.name, file);
    }
  }

  onRemoveFile(key: string) {
    this.files.delete(key);
  }

  onBackCall() {
    this.router.navigate([`/work-details/${this.workId}`])
  }
}
