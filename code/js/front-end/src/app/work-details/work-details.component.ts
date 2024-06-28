import {Component, inject} from '@angular/core';
import {ActivatedRoute, RouterLink} from '@angular/router';
import {LogEntrySimplified, Member, Role, Technician, Work, WorkState} from "../utils/classes";
import {HttpService} from '../utils/http.service';
import {HttpClientModule} from "@angular/common/http";
import {MatTab, MatTabGroup} from "@angular/material/tabs";
import {DatePipe, NgClass, NgForOf, NgIf} from "@angular/common";
import {MatCardModule} from "@angular/material/card";
import {MatListModule} from "@angular/material/list";
import {MatIcon} from "@angular/material/icon";
import {MatButton, MatFabButton, MatIconButton} from "@angular/material/button";
import {MatLabel} from "@angular/material/form-field";
import {FormsModule} from "@angular/forms";
import {MatBadge} from "@angular/material/badge";
import {catchError, throwError} from "rxjs";
import {ErrorHandler} from "../utils/errorHandle";
import {MatDialog} from "@angular/material/dialog";
import {ConfirmDialogComponent} from "../utils/dialogComponent";
import {NavigationService} from "../utils/navService";

@Component({
  selector: 'app-work-details',
  standalone: true,
  imports: [
    HttpClientModule,
    MatTabGroup,
    MatTab,
    NgForOf,
    NgClass,
    MatCardModule,
    MatListModule,
    RouterLink,
    DatePipe,
    MatIcon,
    MatFabButton,
    NgIf,
    MatButton,
    MatLabel,
    FormsModule,
    MatBadge,
    MatIconButton
  ],
  providers: [HttpService],
  templateUrl: './work-details.component.html',
  styleUrl: './work-details.component.css'
})
export class WorkDetailsComponent {
  route: ActivatedRoute = inject(ActivatedRoute);
  httpService = inject(HttpService);
  work: Work | undefined;
  admin: boolean | undefined;
  fiscal: string | undefined;
  diretor: string | undefined;
  coordenador: string | undefined;
  filteredLogList: LogEntrySimplified[] = [];
  filteredMembers: Member[] = [];
  workSrc = ''
  searchLogValue = '';
  searchMemberValue = '';
  tabIndex = 0;

  constructor(private navService: NavigationService, private dialog: MatDialog, private errorHandle: ErrorHandler) {
    const workListingId = String(this.route.snapshot.params['id']);
    this.httpService.getWorkById(workListingId).pipe(
      catchError(error => {
        this.errorHandle.handleError(error);
        return throwError(error);
      })
    ).subscribe((work: Work) => {
      this.work = work;
      this.work.state = WorkState.composeState(work.state);
      this.work.type = WorkState.composeType(work.type);
      this.filteredLogList = work.log;
      this.filteredMembers = work.members;
      work.technicians = this.composeTechnicianRoles(work.technicians)
      this.fiscal = work.members.find(member => member.role === 'FISCALIZAÇÃO')?.name
      this.diretor = work.members.find(member => member.role === 'DIRETOR')?.name
      this.coordenador = work.members.find(member => member.role === 'COORDENADOR')?.name
      this.admin = this.work.members.find(member => member.role === 'ADMIN')?.id === Number(localStorage.getItem('userId'));
    });
    this.httpService.getWorkImage(workListingId).pipe(
      catchError(error => {
        this.errorHandle.handleError(error);
        return throwError(error);
      })
    ).subscribe((data) => {
      if (data.size === 0) {
        this.workSrc = './assets/work.png'
      } else {
        this.workSrc = URL.createObjectURL(data)
      }
    })
  }

  changeTab(id: number) {
    this.tabIndex = id
  }

  onInviteClick() {
    this.navService.navInviteMembers(this.work?.name, this.work?.id)
  }

  createNewEntry() {
    this.navService.navCreateLogEntry(this.work!!.id);
  }

  composeTechnicianRoles(tecs: Technician[]): Technician[] {
    return tecs.map(technician => {
      technician.role = Role.composeRole(technician.role);
      return technician;
    });
  }

  onLogEntryClick(id: number) {
    this.navService.navLogEntry(id)
  }

  onMemberClick(id: number) {
    this.navService.navMemberProfile(id)
  }

  filterResults(text: string) {
    if (!text) {
      this.filteredLogList = this.work!!.log;
      return;
    }
    this.filteredLogList = this.work!!.log.filter(
      entry => entry.title.toLowerCase().includes(text.toLowerCase())
    );
  }

  filterMembers(text: string) {
    if (!text) {
      this.filteredMembers = this.work!!.members;
      return;
    }
    this.filteredMembers = this.work!!.members.filter(
      entry => entry.name.toLowerCase().includes(text.toLowerCase())
    );
  }

  onBackCall() {
    this.navService.back()
  }

  finishWorkCall() {
    const dialogRef = this.dialog.open(ConfirmDialogComponent, {
      data: {
        title: 'Terminar Obra',
        message: 'Tem a certeza que deseja terminar a obra?',
      },
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result === true) {
        this.httpService.finishWork(this.work!!.id).pipe(
          catchError(error => {
            this.errorHandle.handleError(error);
            return throwError(error);
          })
        ).subscribe(() => {
          this.navService.navWork();
        })
      }
    });
  }
}
