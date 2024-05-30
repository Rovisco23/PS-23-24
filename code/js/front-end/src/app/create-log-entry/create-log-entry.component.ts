import {ChangeDetectorRef, Component, inject} from '@angular/core';
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

  logEntry: LogEntryInputModel = {
    workId: '',
    title: '',
    description: '',
    file: new FormData()
  }

  constructor(private router: Router, private cdr: ChangeDetectorRef) {
    this.logEntry.workId = String(this.route.snapshot.params['id'])
  }

  imageCounter: number = 0;
  fileCounter: number = 0;

  filesNames: Map<string,string> = new Map<string, string>();

  onSubmit() {
    this.httpService.createLogEntry(this.logEntry).subscribe(() => {
      this.router.navigate([`/work/${this.logEntry.workId}`])
    })
  }

  onImageUpload(event: any) {
    if(event.target.files.length > 0) {
      const image = event.target.files[0];
      this.imageCounter++;
      const key = "image" + this.imageCounter;
      this.logEntry.file.append(key, image);
      this.filesNames.set(key, image.name.toString())
      console.log("Image added: " + image.name.toString())
      this.cdr.detectChanges()
    }
  }

  onFileUpload(event: any) {
    if(event.target.files.length > 0) {
      const file = event.target.files[0];
      this.fileCounter++;
      const key = "file" + this.imageCounter;
      this.logEntry.file.append(key, file);
      this.filesNames.set(key, file.name.toString())
      console.log("Image added: " + file.name.toString())
      this.cdr.detectChanges()
    }
  }

  onRemoveFile(key: string) {
    this.logEntry.file.delete(key);
    this.filesNames.delete(key);
    this.cdr.detectChanges()
  }
}
