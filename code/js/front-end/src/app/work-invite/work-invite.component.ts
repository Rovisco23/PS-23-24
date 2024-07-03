import {Component, inject, ViewChild} from '@angular/core';
import {ActivatedRoute} from "@angular/router";
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
import {HttpService} from "../utils/http.service";
import {catchError, throwError} from "rxjs";
import {ErrorHandler} from "../utils/errorHandle";
import {NavigationService} from "../utils/navService";
import {WorkDetailsComponent} from "../work-details/work-details.component";

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
    Validators.pattern(/^(ESPECTADOR|MEMBRO|ARQUITETURA|ESTABILIDADE|ELETRICIDADE|GÁS|CANALIZAÇÃO|TELECOMUNICAÇÕES|TERMICO|ACUSTICO|TRANSPORTES)$/)
  ]);

  @ViewChild(MatTable, {static: false}) table: MatTable<Invite> | undefined;

  matcher = new MyErrorStateMatcher()

  workId: string = '';
  invites: Invite[] = [];
  email: string | null = this.emailFormControl.value;
  role: string | null = this.roles.value;
  displayedColumns: string[] = ['email', 'role', 'delete'];
  httpService = inject(HttpService)

  constructor(
    private workComponent: WorkDetailsComponent,
    private route: ActivatedRoute,
    private navService: NavigationService,
    private errorHandle: ErrorHandler
  ) {
    this.workComponent.tabIndex = 2;
    const parentRoute = this.route.parent;
    if (parentRoute) {
      const parentId = parentRoute.snapshot.paramMap.get('id');
      this.workId = parentId ?? '';
    }
    if (!this.workComponent.checkActionPermissions('log')) {
      this.onBackCall()
    }
    this.emailFormControl.valueChanges.subscribe(value => {
      this.email = value;
    });
    this.roles.valueChanges.subscribe(value => {
      this.role = value;
    });
  }

  addInvite(email: string | null, role: string | null) {
    if (!email || !role || this.invites.some(i => i.email === email)) {
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
    this.httpService.inviteMembers(this.workId, this.invites).pipe(
      catchError(error => {
        this.errorHandle.handleError(error);
        return throwError(error);
      })
    ).subscribe(() => {
      this.workComponent.showLayout = true;
      this.navService.navWorkDetails(this.workId);
    });
  }

  onBackCall() {
    this.workComponent.showLayout = true;
    this.navService.navWorkDetails(this.workId);
  }
}
