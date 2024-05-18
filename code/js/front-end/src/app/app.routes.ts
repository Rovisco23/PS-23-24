import { Routes } from '@angular/router';
import { LoginComponent } from "./login/login.component";
import { WorkComponent } from "./work/work.component";
import {TokenGuard} from "./utils/token.guard";
import {WorkDetailsComponent} from "./work-details/work-details.component";
import {SignUpComponent} from "./sign-up/sign-up.component";
import {ProfileComponent} from "./profile/profile.component";
import {EditProfileComponent} from "./edit-profile/edit-profile.component";
import {CreateWorkComponent} from "./create-work/create-work.component";

export const routes: Routes = [
  {
    path: 'work',
    component: WorkComponent,
    canActivate: [TokenGuard],
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
    canActivate: [TokenGuard],
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
    canActivate: [TokenGuard],
  },
  {
    path: 'profile',
    component: ProfileComponent,
    canActivate: [TokenGuard],
    children: [{
      path: 'edit',
      canActivate: [TokenGuard],
      component: EditProfileComponent
    }]
  },
  {
    path: 'edit-profile',
    component: EditProfileComponent,
    canActivate: [TokenGuard],
  },
  {
    path: 'create-work',
    component: CreateWorkComponent,
  },
  {
    path: '**',
    redirectTo: '/work'
  }
];
