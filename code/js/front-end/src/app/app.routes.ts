import { Routes } from '@angular/router';
import { LoginComponent } from "./login/login.component";
import { WorkComponent } from "./work/work.component";
import {TokenGuard} from "./token.guard";
import {WorkDetailsComponent} from "./work-details/work-details.component";

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
    title: 'Work details'
  },
  /*{
    path: 'login',
    component: LoginComponent
  },
  {
    path: '**',
    redirectTo: '/work'
  }*/
];
