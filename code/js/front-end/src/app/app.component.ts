import { Component } from '@angular/core';
import { RouterModule } from '@angular/router';
import {HttpClientModule} from "@angular/common/http";
import {TopbarComponent} from "./topbar/topbar.component";

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterModule, TopbarComponent],
  templateUrl: './app.component.html',
  styleUrl: './app.component.css',
})
export class AppComponent {
  title = 'livro-de-obra-eletronico';
}
