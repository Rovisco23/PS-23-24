import {Component, EventEmitter, inject, Output} from '@angular/core';
import {MatInputModule} from '@angular/material/input';
import {MatFormFieldModule} from "@angular/material/form-field";
import {FormsModule} from '@angular/forms';
import {CommonModule, NgOptimizedImage} from "@angular/common";
import {MatList, MatListItem, MatListItemMeta, MatListItemTitle} from "@angular/material/list";
import {MatIcon} from "@angular/material/icon";
import {MatButton} from "@angular/material/button";
import {ActivatedRoute, Router} from "@angular/router";
import {HttpService} from "../utils/http.service";
import {LogEntryInputModel} from "../utils/classes";

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
    MatButton
  ],
  templateUrl: './create-log-entry.component.html',
  styleUrl: './create-log-entry.component.css'
})
export class CreateLogEntryComponent {
  route: ActivatedRoute = inject(ActivatedRoute);
  httpService = inject(HttpService);
  @Output() imageChanged = new EventEmitter<string>()

  logEntry: LogEntryInputModel = {
    workId: '',
    title: '',
    description: ''
  }

  constructor(private router: Router) {
    this.logEntry.workId = String(this.route.snapshot.params['id'])
  }

  uploadedFiles: string[] = [];

  onSubmit() {
    this.httpService.createLogEntry(this.logEntry).subscribe(() => {
      this.router.navigate([`/work/${this.logEntry.workId}`])
    })
  }

  onFileUpload(event: Event) {
    const fileSrc = event.target as HTMLInputElement;
    const files = fileSrc.files;

    if (files) {
      for (let i = 0; i < files.length; i++) {
        const file = files[i];
        const reader = new FileReader();

        reader.onload = (event: ProgressEvent<FileReader>) => {
          const src = event.target?.result as string;
          this.uploadedFiles.push(src);
        };

        reader.readAsDataURL(file);
      }
    }
  }

  removeFile(file: string) {
    this.uploadedFiles = this.uploadedFiles.filter(src => src !== file);
  }
}
