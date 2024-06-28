import {Injectable} from '@angular/core';
import {MatDialog} from "@angular/material/dialog";
import {ConfirmDialogComponent} from "./dialogComponent";
import {NavigationService} from "./navService";

@Injectable({
  providedIn: 'root',
})
export class ErrorHandler {

  constructor(private dialog: MatDialog, private navService: NavigationService) {
  }

  handleError(error: any) {
    if (error.status === 401) {
      localStorage.removeItem('token')
      localStorage.removeItem('role')
      localStorage.removeItem('userId')
      this.handleErrorInternal(error, () => this.navService.navLogin())
    } else if (error.status === 403) {
      this.handleErrorInternal(error, () => this.navService.back())
    } else if (error.status === 404 || error.status === 400) {
      const nothing = () => {
      }
      this.handleErrorInternal(error, nothing)
    } else {
      this.handleErrorInternal({
        status: error.status,
        error: "Erro inesperado. Tente mais tarde."
      }, () => this.navService.navWork())
    }
  }

  private handleErrorInternal(error: any, onClose: () => any) {
    if (error.error instanceof Blob) {
      error.error.text().then((errorMessage: string) => {
        this.showErrorMessage(errorMessage, error.status, onClose)
      });
    } else {
      this.showErrorMessage(error.error, error.status, onClose)
    }
  }

  private showErrorMessage(message: string, status: number, onClose: () => void) {
    const dialogRef = this.dialog.open(ConfirmDialogComponent, {
      width: '20%',
      height: 'auto',
      data: {
        title: 'Erro ' + status,
        message: message,
      },
    });

    dialogRef.afterClosed().subscribe(() => {
      onClose()
    });
  }

}
