import { Component, OnInit } from '@angular/core';
import { MsalService } from '@azure/msal-angular';

@Component({
  selector: 'app-doctor-dashboard',
  template: `
    <div class="dashboard-container">
      <h2>Panel de Doctor</h2>
      <p>Bienvenido Dr. {{doctorName}}</p>
      <div class="dashboard-content">
        <h3>Información del Doctor</h3>
        <p><strong>Ciudad:</strong> {{city}}</p>
        <p><strong>País:</strong> {{country}}</p>
        <p><strong>Cargo:</strong> {{jobTitle}}</p>
      </div>
    </div>
  `,
  styles: [`
    .dashboard-container {
      padding: 20px;
    }
    .dashboard-content {
      margin-top: 20px;
      padding: 15px;
      border: 1px solid #ddd;
      border-radius: 4px;
    }
  `],
  standalone: true
})
export class DoctorDashboardComponent implements OnInit {
  doctorName = '';
  city = '';
  country = '';
  jobTitle = '';

  constructor(private authService: MsalService) {}

  ngOnInit() {
    const account = this.authService.instance.getAllAccounts()[0];
    if (account?.idTokenClaims) {
      const claims = account.idTokenClaims as any;
      this.city = claims.city || 'No especificado';
      this.country = claims.country || 'No especificado';
      this.jobTitle = claims.jobTitle || 'No especificado';
    }
  }
} 