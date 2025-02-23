import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatCardModule } from '@angular/material/card';
import { MatIconModule } from '@angular/material/icon';
import { BaseChartDirective } from 'ng2-charts';
import { WebSocketService } from '../services/websocket.service';
import { Subscription } from 'rxjs';
import { SignosVitales } from '../models/signos-vitales.model';

@Component({
    selector: 'app-dashboard',
    standalone: true,
    imports: [CommonModule, MatCardModule, MatIconModule, BaseChartDirective],
    template: `
    <div class="dashboard-container">
      <h1>Panel de Control</h1>
      
      <div class="stats-cards">
        <mat-card>
          <h2>Pacientes Críticos</h2>
          <div class="stat-number">{{criticalPatients}}</div>
        </mat-card>

        <mat-card>
          <h2>Alertas Activas</h2>
          <div class="stat-number alert">{{activeAlerts}}</div>
        </mat-card>

        <mat-card>
          <h2>Monitoreo en Tiempo Real</h2>
          <div class="stat-number success">{{activeMonitoring}}</div>
        </mat-card>
      </div>

      <div class="dashboard-grid">
        <mat-card class="chart-card">
          <h2>Signos Vitales</h2>
          <canvas baseChart
            [type]="'line'"
            [datasets]="vitalsChartData.datasets"
            [labels]="vitalsChartData.labels"
            [options]="chartOptions"
            style="width: 100%; height: 100%">
          </canvas>
        </mat-card>

        <mat-card class="alerts-card">
          <h2>Alertas Recientes</h2>
          <div class="alert-item critical">
            <div>
              <h3>Ritmo Cardíaco Elevado</h3>
              <p>Paciente: Juan Pérez</p>
              <small>Hace 2 minutos</small>
            </div>
          </div>
          <div class="alert-item warning">
            <div>
              <h3>Presión Arterial Baja</h3>
              <p>Paciente: María García</p>
              <small>Hace 5 minutos</small>
            </div>
          </div>
          <div class="alert-item critical">
            <div>
              <h3>Nivel de Oxígeno Bajo</h3>
              <p>Paciente: Carlos López</p>
              <small>Hace 10 minutos</small>
            </div>
          </div>
        </mat-card>
      </div>

      <mat-card class="patients-table">
        <h2>Pacientes</h2>
        <table>
          <thead>
            <tr>
              <th>Paciente</th>
              <th>Habitación</th>
              <th>Estado</th>
              <th>Última Actualización</th>
            </tr>
          </thead>
          <tbody>
            <tr>
              <td>Juan Pérez</td>
              <td>101</td>
              <td><span class="status critical">Crítico</span></td>
              <td>Hace 5 min</td>
            </tr>
            <tr>
              <td>María García</td>
              <td>102</td>
              <td><span class="status stable">Estable</span></td>
              <td>Hace 10 min</td>
            </tr>
            <tr>
              <td>Carlos López</td>
              <td>103</td>
              <td><span class="status critical">Crítico</span></td>
              <td>Hace 2 min</td>
            </tr>
          </tbody>
        </table>
      </mat-card>
    </div>
  `,
    styles: [`
    .dashboard-container {
      padding: 20px;
    }

    .stats-cards {
      display: grid;
      grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
      gap: 20px;
      margin-bottom: 20px;
    }

    .stats-cards mat-card {
      padding: 20px;
    }

    .stats-cards h2 {
      margin-top: 0;
      margin-bottom: 10px;
      font-size: 1.1rem;
      color: #666;
    }

    .stat-number {
      font-size: 2.5rem;
      font-weight: bold;
      color: #333;
      margin-top: 10px;
    }

    .stat-number.alert {
      color: #f44336;
    }

    .stat-number.success {
      color: #4caf50;
    }

    .dashboard-grid {
      display: grid;
      grid-template-columns: 2fr 1fr;
      gap: 20px;
      margin-bottom: 20px;
    }

    .chart-card {
      padding: 20px;
      height: 400px;
      position: relative;
    }

    .alerts-card {
      padding: 20px;
    }

    .alert-item {
      padding: 15px;
      border-radius: 8px;
      margin-bottom: 10px;
    }

    .alert-item.critical {
      background-color: #ffebee;
    }

    .alert-item.warning {
      background-color: #fff3e0;
    }

    .alert-item h3 {
      margin: 0;
      color: #d32f2f;
    }

    .alert-item p {
      margin: 5px 0;
    }

    .alert-item small {
      color: #666;
    }

    .patients-table {
      padding: 20px;
    }

    table {
      width: 100%;
      border-collapse: collapse;
    }

    th, td {
      padding: 12px;
      text-align: left;
      border-bottom: 1px solid #ddd;
    }

    .status {
      padding: 4px 8px;
      border-radius: 4px;
      font-size: 0.9em;
    }

    .status.critical {
      background-color: #ffebee;
      color: #d32f2f;
    }

    .status.stable {
      background-color: #e8f5e9;
      color: #2e7d32;
    }
  `]
})
export class DashboardComponent implements OnInit, OnDestroy {
  private wsSubscription!: Subscription;
  criticalPatients = 0;
  activeAlerts = 0;
  activeMonitoring = 0;

  vitalsChartData = {
    labels: [] as string[],
    datasets: [
      {
        label: 'Ritmo Cardíaco',
        data: [] as number[],
        borderColor: '#2196f3',
        tension: 0.4
      },
      {
        label: 'Presión Arterial',
        data: [] as number[],
        borderColor: '#ff9800',
        tension: 0.4
      },
      {
        label: 'Nivel de Oxígeno',
        data: [] as number[],
        borderColor: '#4caf50',
        tension: 0.4
      }
    ]
  };

  chartOptions = {
    responsive: true,
    maintainAspectRatio: false,
    scales: {
      y: {
        beginAtZero: true
      }
    },
    animation: {
      duration: 750
    }
  };

  constructor(private wsService: WebSocketService) {}

  ngOnInit() {
    console.log('Dashboard component initialized');
    this.wsSubscription = this.wsService.getSignosVitales().subscribe(data => {
      console.log('Received vital signs data:', data);
      if (data.length === 0) {
        console.log('No data received');
        return;
      }

      // Update chart data
      this.vitalsChartData = {
        labels: data.map(d => new Date(d.timestamp).toLocaleTimeString()),
        datasets: [
          {
            label: 'Ritmo Cardíaco',
            data: data.map(d => d.frecuenciaCardiaca),
            borderColor: '#2196f3',
            tension: 0.4
          },
          {
            label: 'Presión Arterial',
            data: data.map(d => d.presionArterial),
            borderColor: '#ff9800',
            tension: 0.4
          },
          {
            label: 'Nivel de Oxígeno',
            data: data.map(d => d.saturacionOxigeno),
            borderColor: '#4caf50',
            tension: 0.4
          }
        ]
      };

      // Update statistics
      this.updateStatistics(data);
    });
  }

  private updateStatistics(data: SignosVitales[]) {
    const latestData = data[data.length - 1];
    
    // Update critical patients based on thresholds
    this.criticalPatients = data.filter(d => 
      d.frecuenciaCardiaca > 100 || 
      d.frecuenciaCardiaca < 60 ||
      d.saturacionOxigeno < 95
    ).length;

    // Update active alerts
    this.activeAlerts = this.criticalPatients;

    // Update active monitoring
    this.activeMonitoring = data.length;
  }

  ngOnDestroy() {
    if (this.wsSubscription) {
      this.wsSubscription.unsubscribe();
    }
  }
} 