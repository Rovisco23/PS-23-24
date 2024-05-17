import {Component, inject} from '@angular/core';
import {Router, RouterLink} from "@angular/router";
import {HttpService} from "../http.service";
import {FormsModule} from "@angular/forms";
import {HttpClientModule} from "@angular/common/http";
import {MatButton} from "@angular/material/button";

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [
    HttpClientModule,
    FormsModule,
    MatButton,
    RouterLink
  ],
  templateUrl: './login.component.html',
  styleUrl: './login.component.css',
  providers: [HttpService]
})
export class LoginComponent {
  username: string = '';
  password: string = '';

  httpService = inject(HttpService);

  constructor(private router: Router) {}

  login(): void {
    this.httpService.login(this.username, this.password).subscribe(res => {
      console.log("Done")
      const token = res.token;
      const userId = res.userId;

      localStorage.setItem('userId', userId);
      localStorage.setItem('token', token);
      this.router.navigate(['/work']);
    })
  }
}
