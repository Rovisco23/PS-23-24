import {Injectable} from "@angular/core";
import {HttpClient, HttpHeaders} from "@angular/common/http";
import {Observable} from "rxjs";
import {WorkListing} from "./work-listings/worklisting";

@Injectable({
  providedIn: 'root',
})
export class HttpService {

  constructor(private http: HttpClient) {}

  workListingsList: WorkListing[] = [];

  login(user: string, password: string): Observable<any> {
    return this.http.post<any>('http://localhost:8080/api/login', { user, password })
  }

  logout(token: string): Observable<any> {
    return this.http.post<any>('http://localhost:8080/api/logout', { token })
  }

  signup(email: string, username: string, password: string, firstName: string, lastName: string, nif: number, phone: string, parish: string, county: string, role: string): Observable<any> {
    return this.http.post<any>('http://localhost:8080/api/signup', { email, username, password, firstName, lastName, nif, phone, parish, county, role})
  }

  checkToken(): Observable<any> {
    return this.http.get<any>('http://localhost:8080/api/checkToken')
  }

  getWorkListings(): Observable<any> {
    const token = localStorage.getItem('token');
    const headers = new HttpHeaders().set('Authorization', `Bearer ${token}`);
    return this.http.get<WorkListing[]>('http://localhost:8080/api/work?skip=0', { headers: headers })
  }

  nextPage(offset: number): WorkListing[] {
    return this.workListingsList.slice(offset, 20);
  }

  previousPage(offset: number): WorkListing[] {
    return this.workListingsList.slice(offset - 10, offset);
  }

  getWorkById(id: string): Observable<any> {
    const token = localStorage.getItem('token');
    const headers = new HttpHeaders().set('Authorization', `Bearer ${token}`);
    return this.http.get<WorkListing>(`http://localhost:8080/api/work/${id}`, { headers: headers })
  }
}
