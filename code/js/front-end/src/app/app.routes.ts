import { Routes } from '@angular/router';
import { LoginComponent } from "./login/login.component";
import { WorkComponent } from "./work/work.component";

export const routes: Routes = [
  {
    path: '**',
    redirectTo: '/work'
  },
  {
    path: 'work',
    component: WorkComponent,
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
    path: 'login',
    component: LoginComponent
  }
];
