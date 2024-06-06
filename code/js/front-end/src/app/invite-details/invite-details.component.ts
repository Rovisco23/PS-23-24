import {Component, inject} from '@angular/core';
import {ActivatedRoute, Router} from "@angular/router";
import {HttpService} from "../utils/http.service";
import {AnswerInvite, InviteSimplified, Role} from "../utils/classes";
import {MatCard, MatCardActions, MatCardContent, MatCardHeader, MatCardTitle} from "@angular/material/card";
import {MatFormField} from "@angular/material/form-field";
import {MatInput} from "@angular/material/input";
import {FormsModule} from "@angular/forms";
import {MatButton, MatIconButton} from "@angular/material/button";
import {MatRadioButton} from "@angular/material/radio";
import {MatIcon} from "@angular/material/icon";
import {MatToolbar} from "@angular/material/toolbar";
import {NgIf} from "@angular/common";
import {ErrorHandler} from "../utils/errorHandle";
import {catchError, throwError} from "rxjs";

@Component({
  selector: 'app-invite-details',
  standalone: true,
  imports: [
    MatCardTitle,
    MatCardHeader,
    MatCard,
    MatCardContent,
    MatFormField,
    MatInput,
    FormsModule,
    MatCardActions,
    MatButton,
    MatRadioButton,
    MatIcon,
    MatIconButton,
    MatToolbar,
    NgIf
  ],
  templateUrl: './invite-details.component.html',
  styleUrl: './invite-details.component.css'
})
export class InviteDetailsComponent {

  invite: InviteSimplified | undefined;

  httpService = inject(HttpService);

  constructor(private route: ActivatedRoute, private router: Router, private errorHandle: ErrorHandler) {
    const inviteId =  String(this.route.snapshot.params['id']);
    this.httpService.getInvite(inviteId).pipe(
      catchError(error => {
        this.errorHandle.handleError(error);
        return throwError(error);
      })
    ).subscribe((invite: InviteSimplified) => {
      this.invite = invite;
      this.invite.role = Role.composeRole(invite.role);
    });
  }

  onBackCall() {
    this.router.navigate(['/invites']);
  }

  checkIsNumber(value: string) {
    const x = Number(value);
    if (x <= 0) return false;
    return !isNaN(x);
  }

  onAcceptCall() {
    if (this.invite === undefined) return;
    const accept: AnswerInvite = {
      id: String(this.route.snapshot.params['id']),
      workId: this.invite?.workId,
      accepted: true,
      role: Role.decomposeRole(this.invite.role)
    };
    this.httpService.answerInvite(accept).pipe(
      catchError(error => {
        this.errorHandle.handleError(error);
        return throwError(error);
      })
    ).subscribe(() => {
      this.router.navigate([`/work-details/${this.invite?.workId}`]);
    });
  }

  onRefuseCall() {
    if (this.invite === undefined) return;
    const refuse: AnswerInvite = {
      id: String(this.route.snapshot.params['id']),
      workId: this.invite?.workId,
      accepted: false,
      role: Role.decomposeRole(this.invite.role)
    };
    this.httpService.answerInvite(refuse).pipe(
      catchError(error => {
        this.errorHandle.handleError(error);
        return throwError(error);
      })
    ).subscribe(() => {
      this.router.navigate(['/work']);
    });
  }

}
