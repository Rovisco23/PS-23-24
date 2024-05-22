import {Component, ViewChild} from '@angular/core';
import {Router} from "@angular/router";
import {MatButton, MatFabButton} from "@angular/material/button";
import {Invite, MyErrorStateMatcher} from "../utils/classes";
import {
  MatCell,
  MatCellDef,
  MatColumnDef,
  MatHeaderCell,
  MatHeaderCellDef, MatHeaderRow,
  MatHeaderRowDef, MatRow, MatRowDef,
  MatTable
} from "@angular/material/table";
import {MatIconModule} from "@angular/material/icon";
import {MatError, MatFormField, MatLabel} from "@angular/material/form-field";
import {FormControl, ReactiveFormsModule, Validators} from "@angular/forms";
import {MatInput} from "@angular/material/input";
import {MatOption, MatSelect} from "@angular/material/select";
import {NgIf} from "@angular/common";

@Component({
  selector: 'app-work-invite',
  standalone: true,
  imports: [
    MatButton,
    MatTable,
    MatColumnDef,
    MatHeaderCell,
    MatIconModule,
    MatLabel,
    MatError,
    MatCell,
    MatCellDef,
    MatHeaderCellDef,
    MatHeaderRowDef,
    MatHeaderRow,
    MatRow,
    MatRowDef,
    MatFabButton,
    MatFormField,
    ReactiveFormsModule,
    MatInput,
    MatSelect,
    MatOption,
    NgIf
  ],
  templateUrl: './work-invite.component.html',
  styleUrl: './work-invite.component.css'
})
export class WorkInviteComponent {

  emailFormControl = new FormControl('', [Validators.required, Validators.email]);

  roles = new FormControl('', [
    Validators.required,
    Validators.pattern(/^(CAMARA|TECNICO|MEMBRO)$/)
  ]);

  matcher = new MyErrorStateMatcher()

  workId: string;
  invites: Invite[] = [{ position: 1, email: 'fixolas', role: 'bacans' }];
  email: string | null = this.emailFormControl.value;
  role: string | null = this.roles.value;
  displayedColumns: string[] = ['position', 'email', 'role', 'delete'];

  @ViewChild(MatTable, { static: false }) table: MatTable<Invite> | undefined;

  constructor(private router: Router) {
    this.workId = this.router.getCurrentNavigation()?.extras.state?.['workId'];
  }

  addInvite(email: string | null, role: string | null) {
    if (!email || !role) {
      return;
    }
    const invite: Invite = {
      position: this.invites.length + 1,
      email: email,
      role: role
    };
    this.invites.push(invite);
    this.emailFormControl.reset();
    this.roles.reset();
    this.table?.renderRows();
  }

  removeInvite(position: number) {
    const index = this.invites.findIndex(invite => invite.position === position);
    if (index !== -1) {
      this.invites.splice(index, 1);
      this.invites.forEach((invite, i) => invite.position = i + 1);
      this.table?.renderRows();
    }
  }

  sendInvites() {
    console.log('Invites sent');
  }
}
