import {Injectable} from '@angular/core';
import {HttpClient, HttpHeaders} from "@angular/common/http";
import {Observable} from "rxjs";
import {AnswerInvite, Classes, InputWork, User} from "./classes";

@Injectable({
  providedIn: 'root',
})
export class HttpService {

  constructor(private http: HttpClient) {
  }

  workListingsList: Classes[] = [];

  getTokenHeader() {
    const token = localStorage.getItem('token');
    return new HttpHeaders().set('Authorization', `Bearer ${token}`);
  }

  login(user: string, password: string): Observable<any> {
    return this.http.post<any>('http://localhost:8080/api/login', {user, password})
  }

  logout(token: string): Observable<any> {
    return this.http.post<any>('http://localhost:8080/api/logout', {token})
  }

  signup(email: string, username: string, password: string, firstName: string, lastName: string, nif: number,
         phone: string, parish: string, county: string, role: string, associationName: string, associationNum: number)
    : Observable<any> {
    return this.http.post<any>('http://localhost:8080/api/signup', {
      email, username, password, firstName, lastName, nif, phone, parish, county, role, associationName, associationNum
    })
  }

  checkToken(): Observable<any> {
    return this.http.get<any>('http://localhost:8080/api/checkToken')
  }

  getWorkListings(): Observable<any> {
    const headers = this.getTokenHeader();
    return this.http.get<Classes[]>('http://localhost:8080/api/work?skip=0', {headers: headers})
  }

  nextPage(offset: number): Classes[] {
    return this.workListingsList.slice(offset, 20);
  }

  previousPage(offset: number): Classes[] {
    return this.workListingsList.slice(offset - 10, offset);
  }

  getWorkById(id: string): Observable<any> {
    const headers = this.getTokenHeader();
    return this.http.get<Classes>(`http://localhost:8080/api/work/${id}`, {headers: headers})
  }

  getProfile(id: string): Observable<any> {
    const headers = this.getTokenHeader();
    return this.http.get<any>(`http://localhost:8080/api/users/${id}`, {headers: headers})
  }

  editProfile(user: User) {
    const id = localStorage.getItem('userId');
    const headers = this.getTokenHeader();
    return this.http.put<any>(`http://localhost:8080/api/users/${id}`, {
      username: user.username,
      firstName: user.firstName,
      lastName: user.lastName,
      phone: user.phone,
      parish: user.location.parish,
      county: user.location.county
    }, {headers: headers});
  }

  createWork(work: InputWork): Observable<any> {
    const headers = this.getTokenHeader();
    return this.http.post<any>('http://localhost:8080/api/work', work, {headers: headers})
  }

  createLogEntry(logEntry: FormData): Observable<any> {
    const headers = this.getTokenHeader()
    return this.http.post<any>('http://localhost:8080/api/logs', logEntry, {headers: headers, observe: 'response'})
  }

  getProfilePicture() {
    const headers = this.getTokenHeader();
    return this.http.get<any>('http://localhost:8080/api/profile-picture', {
      headers: headers,
      responseType: 'blob' as 'json'
    })
  }

  inviteMembers(workId: string, invites: any): Observable<any> {
    const headers = this.getTokenHeader();
    return this.http.post<any>(`http://localhost:8080/api/work/${workId}`, invites, {headers: headers})
  }

  getInviteList(): Observable<any> {
    const headers = this.getTokenHeader();
    return this.http.get<any>('http://localhost:8080/api/invite', {headers: headers})
  }

  getInvite(id: string): Observable<any> {
    const headers = this.getTokenHeader();
    return this.http.get<any>(`http://localhost:8080/api/invite/${id}`, {headers: headers})
  }

  answerInvite(answer: AnswerInvite): Observable<any> {
    const headers = this.getTokenHeader();
    return this.http.post<any>('http://localhost:8080/api/invite', answer, {headers: headers})
  }

  changeProfilePicture(form: FormData) {
    const headers = this.getTokenHeader();
    return this.http.put<any>('http://localhost:8080/api/profile-picture', form, {headers: headers})
  }

  getWorkImage(workId: string): Observable<any> {
    const headers = this.getTokenHeader();
    return this.http.get<any>(`http://localhost:8080/api/work-image/${workId}`, {
      headers: headers,
      responseType: 'blob' as 'json'
    })
  }

  getLogById(logId: string): Observable<any> {
    const headers = this.getTokenHeader();
    return this.http.get<any>(`http://localhost:8080/api/logs/${logId}`, {headers: headers})
  }

  editLog(log: FormData, logId: string): Observable<any> {
    const headers = this.getTokenHeader();
    return this.http.put<any>(`http://localhost:8080/api/logs/${logId}`, log, {headers: headers, observe: 'response'})
  }

  getFiles(logId: string, workId: string) {
    const headers = this.getTokenHeader();
    return this.http.post<any>('http://localhost:8080/api/logs-files',
      {
        logId: logId,
        workId: workId
      },
      {
        headers: headers,
        responseType: 'blob' as 'json'
      }
    )
  }
}
