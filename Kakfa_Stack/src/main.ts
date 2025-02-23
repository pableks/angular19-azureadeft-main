import { bootstrapApplication } from '@angular/platform-browser';
import { AppComponent } from './app/app.component';
import { provideHttpClient, withInterceptors } from '@angular/common/http';
import { MSAL_INSTANCE, MSAL_GUARD_CONFIG, MsalGuardConfiguration } from '@azure/msal-angular';
import { authInterceptorProvider } from './app/interceptor/auth-interceptor.interceptor';
import { MSALInstanceFactory } from './app/factories/msal-instance.factory';
import { InteractionType } from '@azure/msal-browser';
import { appConfig } from './app/app.config';

export function MSALGuardConfigFactory(): MsalGuardConfiguration {
  return {
    interactionType: InteractionType.Redirect,
    authRequest: {
      scopes: [],
    },
  };
}

bootstrapApplication(AppComponent, appConfig).catch((err) =>
  console.error(err)
);
