import { Component } from '@angular/core';
import {NgIf} from "@angular/common";
import {Company} from "../utils/classes";

@Component({
  selector: 'app-pdfservice',
  standalone: true,
  imports: [
    NgIf
  ],
  templateUrl: './pdfservice.component.html',
  styleUrl: './pdfservice.component.css'
})
export class PDFServiceComponent {
  openingTerm: OpeningTerm = {
    verification: {
      doc: "doc",
      signature: "signature",
      dt_signature: "11-07-2024"
    },
    location: {
      county: "county",
      parish: "parish",
      street: "street",
      postalCode: "postalCode",
      building: "building"
    },
    licenseHolder: "licenseHolder",
    authors: new Map<string, OpeningTermAuthor>([
      ["fiscalization", {name: "name", association: "association", num: 0}],
      ["coordinator", {name: "name", association: "association", num: 0}],
      ["architect", {name: "name", association: "association", num: 0}],
      ["stability", {name: "name", association: "association", num: 0}],
      ["electricity", {name: "name", association: "association", num: 0}],
      ["gas", {name: "name", association: "association", num: 0}],
      ["water", {name: "name", association: "association", num: 0}],
      ["phone", {name: "name", association: "association", num: 0}],
      ["isolation", {name: "name", association: "association", num: 0}],
      ["acustic", {name: "name", association: "association", num: 0}],
      ["transport", {name: "name", association: "association", num: 0}],
      ["director", {name: "name", association: "association", num: 0}]
    ]),
    company: {
      name: "name",
      num: 0
    },
    type: "type",

  }
}

interface OpeningTermLocation {
  county: string,
  parish: string,
  street: string,
  postalCode: string,
  building: string
}

interface OpeningTermVerification {
  doc: string,
  signature: string,
  dt_signature: string
}

interface OpeningTermAuthor {
  name: string,
  association: string,
  num: number
}

interface OpeningTerm {
  verification: OpeningTermVerification,
  location: OpeningTermLocation,
  licenseHolder: string,
  authors: Map<string, OpeningTermAuthor>,
  company: Company,
  type: string,
}
