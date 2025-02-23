import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatCardModule } from '@angular/material/card';
import { MatIconModule } from '@angular/material/icon';
import { MatSelectModule } from '@angular/material/select';
import { FormsModule } from '@angular/forms';
import { Chart } from 'chart.js/auto';
import { BaseChartDirective } from 'ng2-charts';
import { WebSocketService } from '../services/websocket.service';
import { Subscription } from 'rxjs';
import { SignosVitales } from '../models/signos-vitales.model';
import {
  CategoryScale,
  LinearScale,
  PointElement,
  LineElement,
  Title,
  Tooltip,
  Legend
} from 'chart.js';

// Register Chart.js components
Chart.register(
  CategoryScale,
  LinearScale,
  PointElement,
  LineElement,
  Title,
  Tooltip,
  Legend
);

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [
    CommonModule, 
    MatCardModule, 
    MatIconModule, 
    MatSelectModule,
    FormsModule,
    BaseChartDirective
  ],
  template: `
    <div class="dashboard-container">
      <div class="chart-controls">
        <mat-form-field>
          <mat-label>Paciente</mat-label>
          <mat-select [(value)]="selectedPacienteId" (selectionChange)="updateChartData()">
            <mat-option value="all">Todos los pacientes</mat-option>
            <mat-option *ngFor="let id of pacienteIds" [value]="id">
              Paciente {{id}}
            </mat-option>
          </mat-select>
        </mat-form-field>

        <mat-form-field>
          <mat-label>Intervalo de tiempo</mat-label>
          <mat-select [(value)]="selectedTimeRange" (selectionChange)="updateChartData()">
            <mat-option *ngFor="let range of timeRanges" [value]="range.value">
              {{range.label}}
            </mat-option>
          </mat-select>
        </mat-form-field>
      </div>
      <div class="chart-card">
        <h2>Monitoreo de Signos Vitales en Tiempo Real</h2>
        @if (isConnected) {
          <canvas baseChart
            [type]="'line'"
            [datasets]="vitalsChartData.datasets"
            [labels]="vitalsChartData.labels"
            [options]="chartOptions">
          </canvas>
        } @else {
          <div class="connection-error">
            <mat-icon>error_outline</mat-icon>
            <p>Reconectando al servidor de monitoreo...</p>
          </div>
        }
      </div>
    </div>
  `,
  styles: [`
    .dashboard-container {
      padding: 20px;
    }
    .chart-controls {
      display: flex;
      gap: 20px;
      margin-bottom: 20px;
    }
    .chart-card {
      background: white;
      border-radius: 8px;
      padding: 20px;
      box-shadow: 0 2px 4px rgba(0,0,0,0.1);
      height: 60vh;
      position: relative;
    }
    .connection-error {
      text-align: center;
      padding: 20px;
      color: #f44336;
    }
    .connection-error mat-icon {
      font-size: 48px;
      width: 48px;
      height: 48px;
    }
    canvas {
      max-height: 100%;
      width: 100%!important;
    }
    mat-form-field {
      width: 250px;
    }
  `]
})
export class DashboardComponent implements OnInit, OnDestroy {
  private wsSubscription!: Subscription;
  isConnected = false;
  selectedPacienteId: string = 'all';
  pacienteIds: string[] = [];
  allData: SignosVitales[] = [];
  timeRanges = [
    { value: 1/6, label: 'Últimos 10 segundos' },
    { value: 0.5, label: 'Últimos 30 segundos' },
    { value: 1, label: 'Último minuto' },
    { value: 5, label: 'Últimos 5 minutos' },
    { value: 15, label: 'Últimos 15 minutos' },
    { value: 30, label: 'Últimos 30 minutos' }
  ];
  selectedTimeRange: number = 1/6;

  vitalsChartData = {
    labels: [] as string[],
    datasets: [
      {
        label: 'Frecuencia Cardíaca',
        data: [] as number[],
        borderColor: '#ff4081',
        tension: 0.4
      },
      {
        label: 'Presión Arterial',
        data: [] as number[],
        borderColor: '#3f51b5',
        tension: 0.4
      },
      {
        label: 'Saturación O2',
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
        beginAtZero: true,
        grace: '5%'
      },
      x: {
        grid: {
          display: true
        },
        ticks: {
          maxRotation: 45,
          minRotation: 45
        }
      }
    },
    plugins: {
      legend: {
        position: 'top' as const,
      }
    },
    animation: {
      duration: 750
    }
  };

  constructor(private wsService: WebSocketService) {
    this.wsService.getConnectionStatus().subscribe(
      status => this.isConnected = status
    );
  }

  ngOnInit() {
    this.wsSubscription = this.wsService.getSignosVitales().subscribe(data => {
      this.allData = data;
      
      // Update paciente list
      this.pacienteIds = [...new Set(data.map(d => d.pacienteId))];
      
      this.updateChartData();
    });
  }

  updateChartData() {
    const cutoffTime = new Date();
    // Convert minutes to milliseconds for comparison
    const rangeInMs = this.selectedTimeRange * 60 * 1000;
    cutoffTime.setTime(cutoffTime.getTime() - rangeInMs);

    const filteredData = this.allData.filter(d => {
      const dataTime = new Date(d.timestamp);
      
      // Patient filter
      const patientMatch = this.selectedPacienteId === 'all' || 
                          d.pacienteId === this.selectedPacienteId;
      
      // Time filter
      return dataTime >= cutoffTime && patientMatch;
    });

    // Update chart data
    this.vitalsChartData = {
      labels: filteredData.map(d => 
        new Date(d.timestamp).toLocaleTimeString('es-ES', {
          hour: '2-digit',
          minute: '2-digit',
          second: '2-digit'
        })
      ),
      datasets: [
        {
          ...this.vitalsChartData.datasets[0],
          data: filteredData.map(d => d.frecuenciaCardiaca)
        },
        {
          ...this.vitalsChartData.datasets[1],
          data: filteredData.map(d => d.presionArterial)
        },
        {
          ...this.vitalsChartData.datasets[2],
          data: filteredData.map(d => d.saturacionOxigeno)
        }
      ]
    };
  }

  ngOnDestroy() {
    if (this.wsSubscription) {
      this.wsSubscription.unsubscribe();
    }
  }
}