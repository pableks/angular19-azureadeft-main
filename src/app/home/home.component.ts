import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MsalBroadcastService, MsalService } from '@azure/msal-angular';
import { MatButtonModule } from '@angular/material/button';
import { RouterLink } from '@angular/router';
import {
  AuthenticationResult,
  EventMessage,
  EventType,
  InteractionStatus,
} from '@azure/msal-browser';
import { filter } from 'rxjs/operators';
import { MatIconModule } from '@angular/material/icon';
import { MatCardModule } from '@angular/material/card';

@Component({
    selector: 'app-home',
    templateUrl: './home.component.html',
    styleUrls: ['./home.component.scss'],
    imports: [CommonModule, MatButtonModule, RouterLink, MatIconModule, MatCardModule]
})
export class HomeComponent implements OnInit {
  loginDisplay = false;
  userRole = '';

  constructor(
    private authService: MsalService,
    private msalBroadcastService: MsalBroadcastService
  ) {}

  ngOnInit(): void {
    this.msalBroadcastService.msalSubject$
      .pipe(
        filter((msg: EventMessage) => msg.eventType === EventType.LOGIN_SUCCESS)
      )
      .subscribe((result: EventMessage) => {
        console.log(result);
        const payload = result.payload as AuthenticationResult;
        this.authService.instance.setActiveAccount(payload.account);
        this.checkUserRole();
      });

    this.msalBroadcastService.inProgress$
      .pipe(
        filter((status: InteractionStatus) => status === InteractionStatus.None)
      )
      .subscribe(() => {
        this.setLoginDisplay();
        this.checkUserRole();
      });
  }

  setLoginDisplay() {
    this.loginDisplay = this.authService.instance.getAllAccounts().length > 0;
  }

  checkUserRole() {
    const account = this.authService.instance.getAllAccounts()[0];
    if (account?.idTokenClaims) {
      const claims = account.idTokenClaims as any;
      this.userRole = claims.extension_roles || '';
    }
  }

  login() {
    // Add your login logic here
    console.log('Login clicked');
  }
}
