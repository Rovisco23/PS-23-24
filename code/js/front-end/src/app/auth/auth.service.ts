import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import {Observable} from "rxjs";

@Injectable({
  providedIn: 'root',
})
export class AuthService {

  constructor(private http: HttpClient) { }

  login(user: string, password: string): Observable<any> {
    return this.http.post<any>('http://localhost:8080/api/login', { user, password })
  }

  checkToken(): Observable<any> {
    return this.http.get<any>('http://localhost:8080/api/checkToken')
  }
}
