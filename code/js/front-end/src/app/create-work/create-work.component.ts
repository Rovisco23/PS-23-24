import {Component, inject, ViewChild} from '@angular/core';
import {freguesias, concelhos} from '../utils/utils';
import {HttpService} from "../utils/http.service";
import {InputWork, MyErrorStateMatcher, Technician, WorkTypes} from "../utils/classes";
import {Router} from "@angular/router";
import {FormControl, FormsModule, ReactiveFormsModule, Validators} from "@angular/forms";
import {MatButton, MatFabButton} from "@angular/material/button";
import {MatError, MatFormField, MatLabel, MatOption, MatSelect} from "@angular/material/select";
import {CommonModule, NgForOf, NgIf, Location} from "@angular/common";
import {MatIcon} from "@angular/material/icon";
import {
  MatCell,
  MatCellDef,
  MatColumnDef,
  MatHeaderCell, MatHeaderCellDef,
  MatHeaderRow,
  MatHeaderRowDef,
  MatRow, MatRowDef, MatTable
} from "@angular/material/table";
import {MatInput} from "@angular/material/input";
import {catchError, throwError} from "rxjs";
import {ErrorHandler} from "../utils/errorHandle";
import {NavigationService} from "../utils/navService";

@Component({
  selector: 'app-create-work',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    MatButton,
    ReactiveFormsModule,
    MatSelect,
    MatOption,
    NgForOf,
    NgIf,
    MatFormField,
    MatIcon,
    MatCell,
    MatCellDef,
    MatColumnDef,
    MatFabButton,
    MatHeaderCell,
    MatHeaderRow,
    MatHeaderRowDef,
    MatInput,
    MatLabel,
    MatRow,
    MatRowDef,
    MatTable,
    MatHeaderCellDef,
    MatError
  ],
  templateUrl: './create-work.component.html',
  styleUrl: './create-work.component.css'
})
export class CreateWorkComponent {

  roles = new FormControl('', [
    Validators.required,
    Validators.pattern(/^(ARQUITETURA|ESTABILIDADE|ELETRICIDADE|GÁS|CANALIZAÇÃO|TELECOMUNICAÇÕES|TERMICO|ACUSTICO|TRANSPORTES)$/)
  ]);

  matcher = new MyErrorStateMatcher()
  @ViewChild(MatTable, { static: false }) table: MatTable<Technician> | undefined;

  addTechName = '';
  addTechRole: string | null = this.roles.value;
  addTechAssociation = '';
  addTechNumber = 0;

  work: InputWork;
  types = Object.values(WorkTypes);
  displayedColumns: string[] = ['name', 'role', 'association', 'delete'];


  httpService = inject(HttpService)

  diretor :Technician = {
    name: '',
    role: 'DIRETOR',
    association: {
      name: '',
      number: 0
    }
  }

  fiscal :Technician = {
    name: '',
    role: 'FISCALIZAÇÃO',
    association: {
      name: '',
      number: 0
    }
  }

  coordenador :Technician = {
    name: '',
    role: 'COORDENADOR',
    association: {
      name: '',
      number: 0
    }
  }

  counties: string[] = [];
  parishes: string[] = [];
  districts: string[] = [];

  constructor(private router: Router, private location: Location, private errorHandle: ErrorHandler, private navService: NavigationService) {
    this.work = {
      name: '',
      type: '',
      description: '',
      holder: '',
      company: {
        name: '',
        num: 0
      },
      building: '',
      address: {
        location: {
          district: '',
          county: '',
          parish: ''
        },
        street: '',
        postalCode: ''
      },
      technicians: [],
      verification: false,
      verificationDoc: null
    }

    this.roles.valueChanges.subscribe(value => {
      this.addTechRole = value;
    });
    concelhos.forEach((value, key) => {
      value.forEach((v: string) => this.counties.push(v));
      this.districts.push(key);
    })
    freguesias.forEach((value) => {
      value.forEach((x: string) => this.parishes.push(x));
    })
  }

  formatZipCode() {
    let value = this.work.address.postalCode.replace(/\D/g, '');
    if (value.length > 4) {
      value = `${value.slice(0, 4)}-${value.slice(4, 7)}`;
    }
    this.work.address.postalCode = value;
  }

  updateLocation(change: boolean) {
    const selectedParish = this.work.address.location.parish;
    const selectedCounty = this.work.address.location.county;
    if (!change) {
      const cList: string[] = [];
      const dList: string[] = [];
      for (const c of freguesias.keys()) {
        const pList = freguesias.get(c);
        if (pList.includes(selectedParish)) {
          cList.push(c);
        }
      }
      for (const d of concelhos.keys()) {
        const conList = concelhos.get(d);
        for (const c of cList) {
          if (conList.includes(c)) {
            dList.push(d);
          }
        }
      }
      this.counties = cList;
      this.districts = dList;
      this.work.address.location.county = cList[0];
    }
    if (change) {
      const dList: string[] = [];
      for (const d of concelhos.keys()) {
        const cList = concelhos.get(d);
        if (cList.includes(selectedCounty)) {
          dList.push(d);
        }
      }
      this.districts = dList;
    }
    this.work.address.location.district = this.districts[0];
  }

  create() {
    this.work.type = this.work.type.toUpperCase();
    this.httpService.createWork(this.work).pipe(
      catchError(error => {
        this.errorHandle.handleError(error);
        return throwError(error);
      })
    ).subscribe(() => {
      console.log("Work Created!");
      this.navService.navWork()
    });
  }

  addTechnician() {
    if (!this.addTechName || !this.addTechAssociation || !this.addTechNumber || !this.addTechRole ||
      this.work.technicians.some(t => t.role === this.addTechRole)) {
      return;
    }
    this.work.technicians.push({
      name: this.addTechName,
      role: this.addTechRole,
      association: {
        name: this.addTechAssociation,
        number: this.addTechNumber
      }
    });
    this.addTechName = '';
    this.roles.reset();
    this.addTechAssociation = '';
    this.addTechNumber = 0;
    this.table?.renderRows();
  }

  validateTechnician(tec: Technician) {
    if (tec.name !== '' && tec.association.name !== '' && Number(tec.association.number) !== 0) {
      if (!this.work.technicians.some(t => t.role === tec.role)) this.work.technicians.push(tec);
    }
    else {
      this.work.technicians = this.work.technicians.filter(t => t.role !== tec.role);
    }
    this.table?.renderRows();
  }

  onRemoveTechnician(role: string) {
    this.work.technicians = this.work.technicians.filter(t => t.role !== role);
    this.table?.renderRows();
  }

  toggleVerification(event: any) {
    this.work.verification = event.target.checked;
  }

  onBackCall() {
    this.navService.back()
  }
}
