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

export const routes: Routes = [
  {
    path: 'work',
    component: WorkComponent,
    canActivate: [AuthGuard],
    title: 'Work Page'
    /*children: [
      {
        path: 'create',
        component: WorkCreateComponent
      },
      {
        path: 'obras/:id',
        component: WorkByIdComponent
      },
      {
        path: 'edit/:id',
        component: WorkEditComponent
      },
      {
        path: 'delete/:id',
        component: WorkDeleteComponent
      }
      ]*/
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
    path: 'create-log-entry/:id',
    component: CreateLogEntryComponent,
    canActivate: [AuthGuard],
  },
  {
    path: '**',
    redirectTo: '/work'
  }
];
