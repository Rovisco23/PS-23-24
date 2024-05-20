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
  content: string,
  state: string,
  createdAt: string
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
