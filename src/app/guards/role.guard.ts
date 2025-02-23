import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { MsalService } from '@azure/msal-angular';

export const roleGuard: CanActivateFn = (route) => {
  const msalService = inject(MsalService);
  const router = inject(Router);
  
  const account = msalService.instance.getAllAccounts()[0];
  if (!account?.idTokenClaims) {
    router.navigate(['/unauthorized']);
    return false;
  }

  const claims = account.idTokenClaims as any;
  const requiredRole = route.data['role'];
  const userRole = claims['extension_roles'];

  if (userRole === requiredRole) {
    return true;
  }

  router.navigate(['/unauthorized']);
  return false;
}; 