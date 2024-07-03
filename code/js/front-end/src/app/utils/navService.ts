import {Injectable} from "@angular/core";
import {Router} from "@angular/router";
import {Location} from "@angular/common";

@Injectable({
  providedIn: 'root',
})
export class NavigationService {

  constructor(private router: Router, private location: Location) {
  }

  back() {
    this.location.back();
  }

  navUrl(url: string) {
    this.router.navigate([url]);
  }

  navWork() {
    this.router.navigate(['/work']);
  }

  navLogin() {
    this.router.navigate(['/login']);
  }

  navWorkDetails(workId: string) {
    this.router.navigate([`/work-details/${workId}`]);
  }

  navInviteMembers(id: string) {
    this.router.navigate([`/work-details/${id}/invite-members`])
  }

  navCreateLogEntry(id: string) {
    this.router.navigate([`/work-details/${id}/create-log-entry`]);
  }

  navLogEntry(workId: string, id: number) {
    this.router.navigate([`/work-details/${workId}/log-entry/${id}`]);
  }

  navWorkMemberProfile(workId: string, username: string, extras: any = null) {
    this.router.navigate([`/work-details/${workId}/profile/${username}`], extras);
  }

  navVerifications() {
    this.router.navigate(['/verifications']);
  }

  navCreateWork() {
    this.router.navigate(['/create-work']);
  }

  navSignUp() {
    this.router.navigate(['/signup']);
  }

  navProfile(username: string, extras: any = null) {
    if (extras === null) {
      this.router.navigate([`/profile/${username}`]);
    } else {
      this.router.navigate([`/profile/${username}`], extras);
    }
  }

  navUsers() {
    this.router.navigate(['/users']);
  }

  navPendingUsers() {
    this.router.navigate(['/pending-users']);
  }

  navInvite(id: string) {
    this.router.navigate([`/invites/${id}`]);
  }

  navInviteList() {
    this.router.navigate(['/invites']);
  }
}
