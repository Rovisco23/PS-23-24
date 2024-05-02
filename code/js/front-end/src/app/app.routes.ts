import { Routes } from '@angular/router';

export const routes: Routes = [
  {
    path: '**',
    redirectTo: '/obras'
  },
  {
    path: 'obras',
    component: ObrasComponent,
    children: [
      {
        path: 'create',
        component: ObrasCreateComponent
      },
      {
        path: 'obras/:id',
        component: ObrasByIdComponent
      },
      {
        path: 'edit/:id',
        component: ObrasEditComponent
      },
      {
        path: 'delete/:id',
        component: ObrasDeleteComponent
      }
      ]
  },
  {
    path: 'login',
    component: LoginComponent
  }
];
