import { Component, OnInit } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from 'src/environments/environment';
import { DefaultBackendService } from '../service/default-backend.service';
import { CommonModule } from '@angular/common';
import { MsalService } from '@azure/msal-angular';
import { InteractionType } from '@azure/msal-browser';
import { jwtDecode } from "jwt-decode";
import { InteractionRequiredAuthError } from '@azure/msal-browser';
import { RouterLink } from '@angular/router';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';


type ProfileType = {
  city: string;
  country: string;
  jobTitle: string;
  sub: string;
  // Add any other fields you expect from the token
};

@Component({
    selector: 'app-profile',
    templateUrl: './profile.component.html',
    imports: [CommonModule, RouterLink, MatProgressSpinnerModule],
    styleUrls: ['./profile.component.css']
})
export class ProfileComponent implements OnInit {
  profile?: any;
  responseBackend!: object;
  isAuthenticating = false;
  isLoading = true;

  constructor(
    private authService: MsalService,
    private http: HttpClient,
    private backendService: DefaultBackendService
  ) { }

  ngOnInit() {
    this.getProfile();
  }

  private callProfileApi() {
    try {
      const account = this.authService.instance.getAllAccounts()[0];
      if (account && account.idTokenClaims) {
        console.log('ID Token Claims:', account.idTokenClaims);
        this.profile = account.idTokenClaims;
      }
      this.isLoading = false;
    } catch (e) {
      console.error('Token parsing failed:', e);
      this.isLoading = false;
    }
  }

  getProfile() {
    console.log('getProfile called');
    if (this.isAuthenticating) {
      console.log('Already authenticating, returning');
      return;
    }

    const account = this.authService.instance.getAllAccounts()[0];
    console.log('Account:', account);
    
    if (!account) {
      console.log('No account found, initiating login popup');
      this.isAuthenticating = true;
      this.authService.loginPopup({
        scopes: environment.apiConfig.scopes
      }).subscribe({
        next: (response) => {
          console.log('Login popup response:', response);
          this.isAuthenticating = false;
          if (response.idToken) {
            this.callProfileApi();
          } else {
            console.log('No access token in response');
          }
        },
        error: (error) => {
          this.isAuthenticating = false;
          console.error('Login failed:', error);
        }
      });
      return;
    }

    console.log('Account found, acquiring silent token');
    this.authService.acquireTokenSilent({
      scopes: environment.apiConfig.scopes,
      account: account
    }).subscribe({
      next: (response) => {
        console.log('Token acquired:', response);
        if (response.idToken) {
          this.callProfileApi();
        } else {
          console.log('No access token in silent token response');
        }
      },
      error: (error) => {
        console.error('Token acquisition failed:', error);
        // If silent token acquisition fails, try popup
        console.log('Attempting popup token acquisition');
        this.authService.acquireTokenPopup({
          scopes: environment.apiConfig.scopes,
          account: account
        }).subscribe({
          next: (popupResponse) => {
            console.log('Popup token acquired:', popupResponse);
            if (popupResponse.idToken) {
              this.callProfileApi();
            }
          },
          error: (popupError) => console.error('Popup token acquisition failed:', popupError)
        });
      }
    });
  }

  llamarBackend(): void {
    this.backendService.consumirBackend().subscribe(response => {
      this.responseBackend = response;
    });
  }

  mostrarResponseBackend(): string {
    return JSON.stringify(this.responseBackend);
  }
}
