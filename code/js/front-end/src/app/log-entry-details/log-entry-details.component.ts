import {Component, inject} from '@angular/core';
import {ActivatedRoute, Router} from "@angular/router";
import {HttpService} from "../utils/http.service";
import {LogEntry, SimpleFile} from "../utils/classes";
import {Location, NgIf} from "@angular/common";
import {MatIcon} from "@angular/material/icon";
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import {MatButton, MatFabButton} from "@angular/material/button";
import {
  MatCell,
  MatCellDef,
  MatColumnDef,
  MatHeaderCell,
  MatHeaderCellDef,
  MatHeaderRow,
  MatHeaderRowDef,
  MatRow,
  MatRowDef,
  MatTable,
  MatTableDataSource
} from "@angular/material/table";
import {MatFormField, MatLabel} from "@angular/material/form-field";
import {MatInput} from "@angular/material/input";
import {MatCheckbox} from "@angular/material/checkbox";
import {SelectionModel} from "@angular/cdk/collections";

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
    MatHeaderCellDef,
    MatCheckbox
  ],
  templateUrl: './log-entry-details.component.html',
  styleUrl: './log-entry-details.component.css'
})
export class LogEntryDetailsComponent {
  route: ActivatedRoute = inject(ActivatedRoute);
  httpService = inject(HttpService);
  log: LogEntry | undefined
  logId: string = ''

  displayedColumns: string[] = ['select', 'name', 'type'];
  dataSource = new MatTableDataSource<SimpleFile>();
  selection = new SelectionModel<SimpleFile>(true, []);


  constructor(private router: Router, private location: Location) {
    this.logId = String(this.route.snapshot.params['id']);
    this.httpService.getLogById(this.logId).subscribe((log: LogEntry) => {
      this.log = log;
      this.dataSource.data = log.files;
    })
  }

  isEditable() {
    return this.log!!.editable
  }

  editCall() {
    this.router.navigate([`/edit-log/${this.logId}`]);
  }

  downloadFiles() {
    const downloadFiles = this.log?.files.filter(file => this.selection.isSelected(file))
    this.httpService.downloadFiles(this.logId, this.log!!.workId, downloadFiles!!).subscribe((res) => {
      const link = document.createElement('a')
      link.href = URL.createObjectURL(res)
      link.download = 'files.zip'
      link.click()
    })
  }

  deleteFiles() {
    const filesToDelete = this.log?.files.filter(file => this.selection.isSelected(file))
    this.httpService.deleteFiles(this.logId, this.log!!.workId, filesToDelete!!).subscribe(() => {
      this.dataSource.data = this.dataSource.data.filter(file => !this.selection.isSelected(file))
      this.selection.clear()
    })
  }

  onBackCall() {
    this.location.back()
  }


  /** Whether the number of selected elements matches the total number of rows. */
  isAllSelected() {
    const numSelected = this.selection.selected.length;
    const numRows = this.dataSource.data.length;
    return numSelected === numRows;
  }

  /** Selects all rows if they are not all selected; otherwise clear selection. */
  toggleAllRows() {
    if (this.isAllSelected()) {
      this.selection.clear();
      return;
    }
    this.selection.select(...this.dataSource.data);
  }

  /** The label for the checkbox on the passed row */
  checkboxLabel(row?: SimpleFile): string {
    if (!row) {
      return `${this.isAllSelected() ? 'deselect' : 'select'} all`;
    }
    return `${this.selection.isSelected(row) ? 'deselect' : 'select'} row ${row.id + 1}`;
  }

}
