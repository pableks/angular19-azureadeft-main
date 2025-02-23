import { Routes } from '@angular/router';
import { FailedComponent } from './failed/failed.component';
import { HomeComponent } from './home/home.component';
import { ProfileComponent } from './profile/profile.component';
import { MsalGuard } from '@azure/msal-angular';
import { DoctorDashboardComponent } from './doctor/doctor-dashboard.component';
import { PatientDashboardComponent } from './patient/patient-dashboard.component';
import { UnauthorizedComponent } from './unauthorized/unauthorized.component';
import { roleGuard } from './guards/role.guard';
import { DashboardComponent } from './dashboard/dashboard.component';
import { AlertasComponent } from './alertas/alertas.component';

export const routes: Routes = [
  {
    path: '',
    component: HomeComponent
  },
  {
    path: 'profile',
    component: ProfileComponent,
    canActivate: [MsalGuard]
  },
  {
    path: 'alertas',
    component: AlertasComponent,
    canActivate: [MsalGuard]
  },
  {
    path: 'dashboard',
    component: DashboardComponent,
    canActivate: [MsalGuard, roleGuard],
    data: { role: 'doctor' }
  },
  {
    path: 'patients',
    component: DashboardComponent,
    canActivate: [MsalGuard, roleGuard],
    data: { role: 'doctor' }
  },
  {
    path: 'doctor-dashboard',
    component: DoctorDashboardComponent,
    canActivate: [MsalGuard, roleGuard],
    data: { role: 'doctor' }
  },
  {
    path: 'patient-dashboard',
    component: PatientDashboardComponent,
    canActivate: [MsalGuard, roleGuard],
    data: { role: 'paciente' }
  },
  {
    path: 'unauthorized',
    component: UnauthorizedComponent
  },
  {
    path: 'login-failed',
    component: FailedComponent
  }
];
