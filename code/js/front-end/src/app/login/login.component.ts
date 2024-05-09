import { Component } from '@angular/core';
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

  constructor(private authService: AuthService, private router: Router) {}

  login(): void {
    this.authService.login(this.username, this.password).subscribe(res => {
      console.log("Done")
      const token = res.token;
      const userId = res.userId;

      // Set cookies
      this.setCookie('token', token);
      this.setCookie('userId', userId);
      this.router.navigate(['/work']);
    })
  }

  private setCookie(name: string, value: string, days: number = 1): void {
    const date = new Date();
    date.setTime(date.getTime() + (days * 24 * 60 * 60 * 1000));
    const expires = "expires=" + date.toUTCString();
    document.cookie = name + "=" + value + ";" + expires + ";path=/";
  }
}
