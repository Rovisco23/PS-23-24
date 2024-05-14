import { Injectable } from '@angular/core';
import {WorkListing} from "../work-listings/worklisting";
import {HttpClient, HttpHeaders} from "@angular/common/http";
import {Observable} from "rxjs";

@Injectable({
  providedIn: 'root'
})
export class WorkService {

  constructor(private http: HttpClient) { }

  workListingsList: WorkListing[] = [];

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
