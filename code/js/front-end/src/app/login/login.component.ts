import {Component, inject} from '@angular/core';
import { Router } from "@angular/router";
import {AuthService} from "../auth/auth.service";
import {FormsModule} from "@angular/forms";
import {HttpClientModule} from "@angular/common/http";

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [
    HttpClientModule,
    FormsModule
  ],
  templateUrl: './login.component.html',
  styleUrl: './login.component.css',
  providers: [AuthService]
})
export class LoginComponent {
  username: string = '';
  password: string = '';

  authService = inject(AuthService);

  constructor(private router: Router) {}

  login(): void {
    this.authService.login(this.username, this.password).subscribe(res => {
      console.log("Done")
      const token = res.token;
      const userId = res.userId;

      localStorage.setItem('userId', userId);
      localStorage.setItem('token', token);
      this.router.navigate(['/work']);
    })
  }
}
