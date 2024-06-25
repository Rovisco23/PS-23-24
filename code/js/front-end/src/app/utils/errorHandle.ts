import {Injectable} from '@angular/core';
import {Router} from "@angular/router";
import {MatSnackBar} from "@angular/material/snack-bar";
import {Location} from "@angular/common";

@Injectable({
  providedIn: 'root',
})
export class ErrorHandler {

  constructor(private router: Router, private snackBar: MatSnackBar, private location: Location) {
  }

  handleError(error: any) {
    if (error.status === 401) {
      localStorage.removeItem('token')
      localStorage.removeItem('role')
      localStorage.removeItem('userId')
      this.handleErrorInternal(error)
      this.router.navigate(['/login'])
    } else if (error.status === 403 || error.status === 404) {
      this.handleErrorInternal(error)
      this.location.back()
    } else {
      this.handleErrorInternal(error)
    }
  }

  private handleErrorInternal(error: any) {
    if (error.error instanceof Blob) {
      error.error.text().then((errorMessage: string) => {
        this.showErrorMessage(errorMessage, error.status)
      });
    } else {
      this.showErrorMessage(error.error, error.status)
    }
  }

  private showErrorMessage(message: string, status: number) {
    this.snackBar.open("Error " + status + ": " + message, 'Close', {
      duration: 5000, // Duration in milliseconds
      verticalPosition: 'top', // Position on the screen
    });
  }

}
