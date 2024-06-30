import { Component, Inject, ViewEncapsulation } from '@angular/core';
import {
  MatDialogRef,
  MAT_DIALOG_DATA,
  MatDialogContent,
  MatDialogActions,
  MatDialogClose, MatDialogTitle
} from '@angular/material/dialog';
import {MatButton} from "@angular/material/button";
import {NgIf} from "@angular/common";

@Component({
  selector: 'app-confirm-dialog',
  template: `
    <div class="error-dialog">
      <h2 mat-dialog-title style="color: #FF7A00">{{ data.title }}</h2>
      <mat-dialog-content style="color: white">
        <p>{{ data.message }}</p>
      </mat-dialog-content>
      <mat-dialog-actions style="justify-content: center">
        <button *ngIf="!(data.title.includes('Erro'))" class="submit-button" mat-button mat-dialog-close>Cancelar
        </button>
        <button class="submit-button" mat-button [mat-dialog-close]="true">Ok</button>
      </mat-dialog-actions>
    </div>
  `,
  styles: [`
    .error-dialog {
      background-color: #11113D;
    }

    .submit-button {
      color: #FF7A00 !important;
      border: 1px solid #FF7A00;
      background-color: #11113D;
    }

    .submit-button:hover {
      color: white !important;
      background-color: #FF7A00;
    }
  `],
  standalone: true,
  imports: [
    MatDialogActions,
    MatDialogContent,
    MatDialogTitle,
    NgIf,
    MatButton,
    MatDialogClose
  ],
  encapsulation: ViewEncapsulation.None
})
export class ConfirmDialogComponent {
  constructor(
    public dialogRef: MatDialogRef<ConfirmDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: any
  ) {}
}
