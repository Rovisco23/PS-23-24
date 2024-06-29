import { Routes } from '@angular/router';
import { LoginComponent } from "./login/login.component";
import { WorkComponent } from "./work/work.component";
import {WorkDetailsComponent} from "./work-details/work-details.component";
import {SignUpComponent} from "./sign-up/sign-up.component";
import {ProfileComponent} from "./profile/profile.component";
import {EditProfileComponent} from "./edit-profile/edit-profile.component";
import {CreateWorkComponent} from "./create-work/create-work.component";
import {LogEntryDetailsComponent} from "./log-entry-details/log-entry-details.component";
import {CreateLogEntryComponent} from "./create-log-entry/create-log-entry.component";
import {WorkInviteComponent} from "./work-invite/work-invite.component";
import {InviteListComponent} from "./invite-list/invite-list.component";
import {InviteDetailsComponent} from "./invite-details/invite-details.component";
import {EditLogEntryComponent} from "./edit-log-entry/edit-log-entry.component";
import {PendingUsersComponent} from "./pending-users/pending-users.component";
import {AdminGuard} from "./utils/admin.guard";
import {ListUsersComponent} from "./list-users/list-users.component";
import {WorkVerificationsComponent} from "./work-verifications/work-verifications.component";
import {CouncilGuard} from "./utils/council.guard";
export const routes: Routes = [
  {
    path: 'work',
    component: WorkComponent,
    title: 'Work Page'
  },
  {
    path: 'work-details/:id',
    component: WorkDetailsComponent,
    title: 'Work details'
  },
  {
    path: 'login',
    component: LoginComponent
  },
  {
    path: 'signup',
    component: SignUpComponent
  },
  {
    path: 'profile/:name',
    component: ProfileComponent,
    children: [{
      path: 'edit',
      component: EditProfileComponent
    }]
  },
  {
    path: 'edit-profile/:name',
    component: EditProfileComponent,
  },
  {
    path: 'create-work',
    component: CreateWorkComponent,
  },
  {
    path: 'invite-members',
    component: WorkInviteComponent,
  },
  {
    path: 'log-entry/:id',
    component: LogEntryDetailsComponent,
  },
  {
    path: 'edit-log/:id',
    component: EditLogEntryComponent,
  },
  {
    path: 'create-log-entry/:id',
    component: CreateLogEntryComponent,
  },
  {
    path: 'invites',
    component: InviteListComponent,
  },
  {
    path: 'invites/:id',
    component: InviteDetailsComponent,
  },
  {
    path: 'pending-users',
    component: PendingUsersComponent,
    canActivate: [AdminGuard]
  },
  {
    path: 'users',
    component: ListUsersComponent,
    canActivate: [AdminGuard]
  },
  {
    path: 'verifications',
    component: WorkVerificationsComponent,
    canActivate: [CouncilGuard]
  },
  {
    path: '**',
    redirectTo: '/work'
  }
];
