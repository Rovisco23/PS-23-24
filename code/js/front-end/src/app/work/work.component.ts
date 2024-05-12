import {Component} from '@angular/core';
import {HttpClientModule} from "@angular/common/http";
import {TopbarComponent} from "../topbar/topbar.component";
import {SidebarComponent} from "../sidebar/sidebar.component";

@Component({
  selector: 'app-work',
  standalone: true,
  imports: [
    TopbarComponent,
    SidebarComponent
  ],
  templateUrl: './work.component.html',
  styleUrl: './work.component.css'
})
export class WorkComponent {

}
