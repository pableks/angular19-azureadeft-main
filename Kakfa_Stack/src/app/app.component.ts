import { Component, OnInit, Inject, OnDestroy } from '@angular/core';
import { MatSidenavModule } from '@angular/material/sidenav';
import { MatListModule } from '@angular/material/list';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { RouterLink, RouterOutlet } from '@angular/router';
import {
  MsalService,
  MsalModule,
  MsalBroadcastService,
  MSAL_GUARD_CONFIG,
  MsalGuardConfiguration,
} from '@azure/msal-angular';
import {
  AuthenticationResult,
  InteractionStatus,
  PopupRequest,
  RedirectRequest,
  EventMessage,
  EventType,
} from '@azure/msal-browser';
import { Subject } from 'rxjs';
import { filter, takeUntil } from 'rxjs/operators';
import { CommonModule } from '@angular/common';
import { environment } from 'src/environments/environment';

@Component({
    selector: 'app-root',
    templateUrl: './app.component.html',
    styleUrls: ['./app.component.css'],
    imports: [
        CommonModule,
        MsalModule,
        RouterOutlet,
        RouterLink,
        MatSidenavModule,
        MatListModule,
        MatIconModule,
        MatButtonModule,
    ],
    standalone: true
})
export class AppComponent implements OnInit, OnDestroy {
  title = 'Angular 18 Ejemplo utilizando - MSAL Angular v3';
  isIframe = false;
  loginDisplay = false;
  isSidenavOpen = false;
  userRole = '';
  private readonly _destroying$ = new Subject<void>();

  constructor(
    @Inject(MSAL_GUARD_CONFIG) private msalGuardConfig: MsalGuardConfiguration,
    private authService: MsalService,
    private msalBroadcastService: MsalBroadcastService
  ) {}

  ngOnInit(): void {
    this.isIframe = window !== window.parent && !window.opener;
  
    this.authService.handleRedirectObservable().subscribe({
      next: (result) => {
        if (result) {
          this.authService.instance.setActiveAccount(result.account);
        }
        this.setLoginDisplay();
      },
      error: (error) => {
        console.error('MSAL Redirect Error: ', error);
      }
    });
  
    this.msalBroadcastService.inProgress$
      .pipe(
        filter((status: InteractionStatus) => status === InteractionStatus.None),
        takeUntil(this._destroying$)
      )
      .subscribe(() => {
        this.setLoginDisplay();
        this.checkAndSetActiveAccount();
      });
  
    this.msalBroadcastService.msalSubject$
      .pipe(
        filter((msg: EventMessage) => 
          msg.eventType === EventType.LOGIN_SUCCESS || 
          msg.eventType === EventType.ACQUIRE_TOKEN_SUCCESS
        ),
        takeUntil(this._destroying$)
      )
      .subscribe((result: EventMessage) => {
        const payload = result.payload as AuthenticationResult;
        if (payload && payload.account) {
          this.authService.instance.setActiveAccount(payload.account);
        }
      });
  }
  setLoginDisplay() {
    this.loginDisplay = this.authService.instance.getAllAccounts().length > 0;
    this.checkUserRole();
  }

  checkAndSetActiveAccount() {
    /**
     * If no active account set but there are accounts signed in, sets first account to active account
     * To use active account set here, subscribe to inProgress$ first in your component
     * Note: Basic usage demonstrated. Your app may require more complicated account selection logic
     */
    let activeAccount = this.authService.instance.getActiveAccount();

    if (
      !activeAccount &&
      this.authService.instance.getAllAccounts().length > 0
    ) {
      let accounts = this.authService.instance.getAllAccounts();
      this.authService.instance.setActiveAccount(accounts[0]);
    }
  }

  loginRedirect() {
    if (this.msalGuardConfig.authRequest) {
      this.authService.loginRedirect({
        ...this.msalGuardConfig.authRequest,
      } as RedirectRequest);
    } else {
      this.authService.loginRedirect();
    }
  }

  loginPopup() {
    if (this.msalGuardConfig.authRequest) {
      this.authService
        .loginPopup({ ...this.msalGuardConfig.authRequest } as PopupRequest)
        .subscribe((response: AuthenticationResult) => {
          this.authService.instance.setActiveAccount(response.account);
  
          // Obtener y guardar el token de acceso
          this.authService.acquireTokenSilent({ scopes: environment.apiConfig.scopes }).subscribe({
            next: (tokenResponse) => {
              localStorage.setItem('jwt', tokenResponse.idToken); // Guarda el token en el localStorage
              console.log('ID token guardado en localStorage:', tokenResponse.idToken);
            },
            error: (error) => {
              console.error('Error obteniendo el token de acceso:', error);
            },
          });
        });
    } else {
      this.authService
        .loginPopup()
        .subscribe((response: AuthenticationResult) => {
          this.authService.instance.setActiveAccount(response.account);
  
          // Obtener y guardar el token de acceso
          this.authService.acquireTokenSilent({ scopes: environment.apiConfig.scopes }).subscribe({
            next: (tokenResponse) => {
              localStorage.setItem('jwt', tokenResponse.accessToken);
              console.log('ID token guardado en localStorage:', tokenResponse.accessToken);
            },
            error: (error) => {
              console.error('Error obteniendo el token de acceso:', error);
            },
          });
        });
    }
  }
  

  logout(popup?: boolean) {
    if (popup) {
      this.authService.logoutPopup({
        mainWindowRedirectUri: '/',
      });
    } else {
      this.authService.logoutRedirect();
    }
  }

  toggleSidenav() {
    this.isSidenavOpen = !this.isSidenavOpen;
  }

  checkUserRole() {
    const account = this.authService.instance.getAllAccounts()[0];
    if (account?.idTokenClaims) {
      const claims = account.idTokenClaims as any;
      this.userRole = claims.extension_roles || '';
    }
  }

  ngOnDestroy(): void {
    this._destroying$.next(undefined);
    this._destroying$.complete();
  }
}
