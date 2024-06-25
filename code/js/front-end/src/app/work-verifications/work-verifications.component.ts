import {Component, ErrorHandler} from '@angular/core';
import {MatDivider} from "@angular/material/divider";
import {MatIcon} from "@angular/material/icon";
import {MatList, MatListItem, MatListItemLine, MatListItemTitle} from "@angular/material/list";
import {Location, NgForOf} from "@angular/common";
import {Router} from "@angular/router";
import {Verification} from "../utils/classes";
import {FormsModule} from "@angular/forms";

@Component({
  selector: 'app-work-verifications',
  standalone: true,
  imports: [
    MatDivider,
    MatIcon,
    MatList,
    MatListItem,
    MatListItemLine,
    MatListItemTitle,
    NgForOf,
    FormsModule
  ],
  templateUrl: './work-verifications.component.html',
  styleUrl: './work-verifications.component.css'
})
export class WorkVerificationsComponent {

  verifications: Verification[] = [{
    workId: '1',
    workTitle: 'Teste',
    id: '1',
    admin: 'admin'
  },{
    workId: '2',
    workTitle: 'Teste2',
    id: '2',
    admin: 'admin2'
  }];
  filteredVerifications: Verification[] = [{
    workId: '1',
    workTitle: 'Teste',
    id: '1',
    admin: 'admin'
  },{
    workId: '2',
    workTitle: 'Teste2',
    id: '2',
    admin: 'admin2'
  }];

  inputValue: string = '';

  constructor(private router: Router, private location: Location, private errorHandle: ErrorHandler) {

  }

  filterVerifications(text: string) {
    if (!text) {
      this.filteredVerifications = this.verifications;
      return;
    }
    this.filteredVerifications = this.verifications.filter(
      entry => entry.workTitle.toLowerCase().includes(text.toLowerCase())
    );
  }

  onVerificationClick(id: string) {
    this.router.navigate([`/verifications/${id}`]);
  }

  onBackCall(){
    this.location.back();
  }
}
