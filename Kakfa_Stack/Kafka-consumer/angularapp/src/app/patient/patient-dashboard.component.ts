import { Component, OnInit } from '@angular/core';
import { MsalService } from '@azure/msal-angular';

@Component({
  selector: 'app-patient-dashboard',
  template: `
    <div class="dashboard-container">
      <h2>Panel de Paciente</h2>
      <p>Bienvenido</p>
      <div class="dashboard-content">
        <h3>Información del Paciente</h3>
        <p><strong>Ciudad:</strong> {{city}}</p>
        <p><strong>País:</strong> {{country}}</p>
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
export class PatientDashboardComponent implements OnInit {
  city = '';
  country = '';

  constructor(private authService: MsalService) {}

  ngOnInit() {
    const account = this.authService.instance.getAllAccounts()[0];
    if (account?.idTokenClaims) {
      const claims = account.idTokenClaims as any;
      this.city = claims.city || 'No especificado';
      this.country = claims.country || 'No especificado';
    }
  }
} 