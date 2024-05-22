import {FormControl, FormGroupDirective, NgForm} from "@angular/forms";
import {ErrorStateMatcher} from "@angular/material/core";

export interface Classes {
  id: string;
  name: string;
  description: string;
  address: Address;
  type: string;
  state: string;
}

interface Address {
  location: Location,
  street: String,
  postalCode: String
}

interface Location {
  district: String,
  county: String,
  parish: String
}

export interface Member {
  id: number;
  name: string;
  role: string;
}

export interface Work {
  id: string;
  name: string;
  description: string;
  address: Address;
  type: string;
  state: string;
  members: Member[];
  log: LogEntrySimplified[];
}

export interface LogEntrySimplified {
  id: number,
  author: Author,
  title: string,
  state: string,
  createdAt: string
}

export interface LogEntryInputModel {
  workId: string,
  title: string,
  description: string
}

interface Author {
  id: number,
  name: string,
  role: string
}

export interface Company {
  name: string;
  num: number;
}

export interface User {
  id: String
  username: String,
  email: String,
  phone: String | null,
  firstName: String,
  lastName: String,
  role: String,
  location: Location
}

export interface InputWork {
  name: string;
  type: string;
  description: string;
  holder: string;
  director: string;
  company: Company;
  building: string;
  address: Address
}

export enum  WorkTypes {
  Residential = 'RESIDENCIAL',
  Comercial ='COMERCIAL',
  Industrial = 'INDUSTRIAL',
  Infrastructural = 'INFRAESTRUTURA',
  Institutional = 'INSTITUCIONAL',
  Rehabilitation ='REABILITAÇÃO',
  Special_Structure = 'ESTRUTURA ESPECIAL',
  Work_of_Art = 'OBRA DE ARTE',
  Habitation = 'HABITAÇÃO',
  Special_Building ='EDIFICIOS ESPECIAL'
}

export interface Invite {
  position: number;
  email: string;
  role: string;
}

export class MyErrorStateMatcher implements ErrorStateMatcher {
  isErrorState(control: FormControl | null, form: FormGroupDirective | NgForm | null): boolean {
    const isSubmitted = form && form.submitted;
    return !!(control && control.invalid && (control.dirty || control.touched || isSubmitted));
  }
}
