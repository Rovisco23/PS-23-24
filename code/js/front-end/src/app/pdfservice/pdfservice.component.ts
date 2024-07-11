import {Component, ElementRef, ViewChild} from '@angular/core';
import {NgIf} from "@angular/common";
import {OpeningTerm, OpeningTermAuthor} from "../utils/classes";
import jsPDF from "jspdf";

@Component({
  selector: 'app-pdfservice',
  standalone: true,
  imports: [
    NgIf
  ],
  templateUrl: './pdfservice.component.html',
  styleUrls: ['./pdfservice.component.css']
})
export class PDFServiceComponent {

  @ViewChild('content', {static: false}) element!: ElementRef;

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
  };

  generatePdf() {
    const doc = new jsPDF('p', 'mm', 'a4');

    const content = this.element.nativeElement;

    return doc.splitTextToSize(content.innerText, 180)
  }
}
