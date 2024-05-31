import {Injectable} from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class PreviousUrlService {
  private previousUrl: string | null = null;

  setPreviousUrl(url: string) {
    this.previousUrl = url;
  }

  getPreviousUrl(): string | null {
    return this.previousUrl;
  }
}
