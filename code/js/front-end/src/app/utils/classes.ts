import {FormControl, FormGroupDirective, NgForm} from "@angular/forms";
import {ErrorStateMatcher} from "@angular/material/core";

export interface Classes {
  id: string;
  name: string;
  owner: string;
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

export interface Pending {
  id: number,
  email: string,
  nif: number,
  location: Location,
  association: Association
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
  licenseHolder: string;
  state: string;
  company: Company;
  building: string;
  members: Member[];
  log: LogEntrySimplified[];
  technicians: Technician[];
  images: number;
  docs: number;
}

export interface LogEntrySimplified {
  id: number,
  author: Author,
  title: string,
  state: string,
  createdAt: string
}

export interface LogEntry {
  workId: string,
  title: string,
  content: string,
  editable: boolean,
  createdAt: string,
  modifiedAt: string,
  author: Author,
  files: SimpleFile[]
}

export interface SimpleFile {
  id: number;
  fileName: string;
  contentType: string;
}

export interface LogEditableEntry {
  workId: string,
  title: string,
  content: string
}

export interface InviteSimplified {
  id: string,
  workId: string,
  workTitle: string,
  admin: string,
  role: string
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
  nif: number,
  firstName: String,
  lastName: String,
  role: String,
  location: Location,
  association: Association
}

export interface InputWork {
  name: string;
  type: string;
  description: string;
  holder: string;
  company: Company;
  building: string;
  address: Address;
  technicians: Technician[];
}

export interface Technician {
  name: string;
  role: string;
  association: Association;
}

export enum WorkTypes {
  Residential = 'Residencial',
  Comercial = 'Comercial',
  Industrial = 'Industrial',
  Infrastructural = 'Infraestrutura',
  Institutional = 'Institucional',
  Rehabilitation = 'Reabilitação',
  Special_Structure = 'Estrutura Especial',
  Work_of_Art = 'Obra De Arte',
  Habitation = 'Habitação',
  Special_Building = 'Edificio Especial'
}

export interface Invite {
  position: number;
  email: string;
  role: string;
}

export interface AnswerInvite {
  id: string;
  workId: string | null;
  accepted: boolean;
  role: string;
}

export interface Association {
  name: string;
  number: number;
}

export class MyErrorStateMatcher implements ErrorStateMatcher {
  isErrorState(control: FormControl | null, form: FormGroupDirective | NgForm | null): boolean {
    const isSubmitted = form && form.submitted;
    return !!(control && control.invalid && (control.dirty || control.touched || isSubmitted));
  }
}

export interface Verification {
  id: string;
  workId: string;
  workTitle: string;
  admin: string;
}

export class Role {

  private static composedRoles: { [key: string]: string } = {
    'MEMBRO': 'Membro',
    'ESPECTADOR': 'Espectador',
    'FISCALIZAÇÃO': 'Responsável de Fiscalização',
    'COORDENADOR': 'Coordenador',
    'ARQUITETURA': 'Técnico de Arquitetura',
    'ESTABILIDADE': 'Técnico de Estabilidade',
    'ELETRICIDADE': 'Técnico de Alimentação e Destribuição de Energia Elétrica',
    'GÁS': 'Técnico de Instalações de Gás',
    'CANALIZAÇÃO': 'Técnico de Instalações de Água e Esgotos',
    'TELECOMUNICAÇÕES': 'Técnico de Instalações de Telecomunicações',
    'TERMICO': 'Técnico de Comportamento Térmico',
    'ACUSTICO': 'Técnico de Condicionamento Acústico',
    'TRANSPORTES': 'Técnico de Instalações de Eletromecânicas de Transporte',
    'DIRETOR': 'Diretor de Obra'
  }

  private static invertedRoles = Role.getInvertedRoles();

  public static getInvertedRoles(): { [key: string]: string } {
    const inverted: { [key: string]: string } = {};
    for (const key in Role.composedRoles) {
      if (Role.composedRoles.hasOwnProperty(key)) {
        inverted[Role.composedRoles[key]] = key;
      }
    }
    return inverted;
  }

  public static composeRole(role: string): string {
    return Role.composedRoles[role] || role;
  }

  public static decomposeRole(roleDescription: string): string {
    return Role.invertedRoles[roleDescription] || roleDescription;
  }
}
