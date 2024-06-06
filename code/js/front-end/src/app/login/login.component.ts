import {Component, inject} from '@angular/core';
import {Router, RouterLink} from "@angular/router";
import {HttpService} from "../utils/http.service";
import {FormsModule} from "@angular/forms";
import {HttpClientModule} from "@angular/common/http";
import {MatButton} from "@angular/material/button";
import {OriginalUrlService} from "../utils/originalUrl.service";
import {catchError, throwError} from "rxjs";
import {ErrorHandler} from "../utils/errorHandle";

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

  constructor(private router: Router, private originalUrlService: OriginalUrlService, private errorHandle: ErrorHandler) {}

  login(): void {
    this.httpService.login(this.username, this.password).pipe(
      catchError(error => {
        this.errorHandle.handleError(error);
        return throwError(error);
      })
    ).subscribe(res => {
      console.log("Done")
      const token = res.token
      const userId = res.userId
      const bytes = res.pfp
      const role = res.role

      localStorage.setItem('userId', userId)
      localStorage.setItem('token', token)
      localStorage.setItem('role', role)
      if (bytes) {
        localStorage.setItem('pfp', window.btoa(bytes))
      }
      const redirect = this.originalUrlService.getOriginalUrl()
      if (redirect) {
        this.originalUrlService.resetOriginalUrl()
        this.router.navigate([redirect])
      } else {
        this.router.navigate(['/work'])
      }
    })
  }
}
