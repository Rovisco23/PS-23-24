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
import {AuthGuard} from "./utils/auth.guard";
import {InviteListComponent} from "./invite-list/invite-list.component";
import {InviteDetailsComponent} from "./invite-details/invite-details.component";
import {EditLogEntryComponent} from "./edit-log-entry/edit-log-entry.component";
import {PendingUsersComponent} from "./pending-users/pending-users.component";
import {AdminGuard} from "./utils/admin.guard";
import {ListUsersComponent} from "./list-users/list-users.component";

export const routes: Routes = [
  {
    path: 'work',
    component: WorkComponent,
    title: 'Work Page'
  },
  {
    path: 'work-details/:id',
    component: WorkDetailsComponent,
    canActivate: [AuthGuard],
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
    path: 'profile/:id',
    component: ProfileComponent,
    canActivate: [AuthGuard],
  },
  {
    path: 'profile',
    component: ProfileComponent,
    canActivate: [AuthGuard],
    children: [{
      path: 'edit',
      canActivate: [AuthGuard],
      component: EditProfileComponent
    }]
  },
  {
    path: 'edit-profile',
    component: EditProfileComponent,
    canActivate: [AuthGuard],
  },
  {
    path: 'create-work',
    component: CreateWorkComponent,
    canActivate: [AuthGuard],
  },
  {
    path: 'invite-members',
    component: WorkInviteComponent,
    canActivate: [AuthGuard]
  },
  {
    path: 'log-entry/:id',
    component: LogEntryDetailsComponent,
    canActivate: [AuthGuard],
  },
  {
    path: 'edit-log/:id',
    component: EditLogEntryComponent,
    canActivate: [AuthGuard],
  },
  {
    path: 'create-log-entry/:id',
    component: CreateLogEntryComponent,
    canActivate: [AuthGuard],
  },
  {
    path: 'invites',
    component: InviteListComponent,
    canActivate: [AuthGuard]
  },
  {
    path: 'invites/:id',
    component: InviteDetailsComponent,
    canActivate: [AuthGuard]
  },
  {
    path: 'pending-users',
    component: PendingUsersComponent,
    canActivate: [AuthGuard, AdminGuard]
  },
  {
    path: 'users',
    component: ListUsersComponent,
    canActivate: [AuthGuard, AdminGuard]
  },
  {
    path: '**',
    redirectTo: '/work'
  }
];
