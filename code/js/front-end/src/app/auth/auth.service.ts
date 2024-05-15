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

  signup(email: string, username: string, password: string, firstName: string, lastName: string, nif: number, phone: string, parish: string, county: string, role: string): Observable<any> {
    return this.http.post<any>('http://localhost:8080/api/signup', { email, username, password, firstName, lastName, nif, phone, parish, county, role})
  }

  checkToken(): Observable<any> {
    return this.http.get<any>('http://localhost:8080/api/checkToken')
  }
}
