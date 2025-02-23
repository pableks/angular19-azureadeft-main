import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatCardModule } from '@angular/material/card';
import { MatIconModule } from '@angular/material/icon';
import { MatBadgeModule } from '@angular/material/badge';
import { AlertaWebSocketService } from '../services/alerta-websocket.service';
import { Subscription } from 'rxjs';
import { Alerta } from '../models/alerta.model';

@Component({
  selector: 'app-alertas',
  standalone: true,
  imports: [CommonModule, MatCardModule, MatIconModule, MatBadgeModule],
  providers: [AlertaWebSocketService],
  template: `
    <div class="alertas-container">
      <h2>Alertas en Tiempo Real</h2>
      @if (!isConnected) {
        <div class="connection-error">
          <mat-icon>error_outline</mat-icon>
          <p>Reconectando al servidor de alertas...</p>
        </div>
      }
      <div class="alertas-list">
        @for (alerta of alertas; track alerta.id) {
          <mat-card class="alerta-card" [ngClass]="alerta.tipo.toLowerCase()">
            <mat-card-header>
              <mat-icon mat-card-avatar>notification_important</mat-icon>
              <mat-card-title>Alerta: {{alerta.tipo}}</mat-card-title>
              <mat-card-subtitle>
                {{alerta.fecha | date:'medium'}}
              </mat-card-subtitle>
            </mat-card-header>
            <mat-card-content>
              <p>{{alerta.descripcion}}</p>
              @if (alerta.pacienteId) {
                <p class="patient-info">
                  Paciente: {{alerta.pacienteNombre || 'No especificado'}} (ID: {{alerta.pacienteId}})
                </p>
              }
            </mat-card-content>
          </mat-card>
        }
      </div>
    </div>
  `,
  styles: [`
    .alertas-container {
      padding: 20px;
    }
    .alertas-list {
      display: flex;
      flex-direction: column;
      gap: 16px;
      margin-top: 20px;
    }
    .alerta-card {
      border-left: 4px solid #ff1744;
      transition: all 0.3s ease;
      animation: slideIn 0.5s ease-out;
    }
    .alerta-card.critica {
      border-color: #ff1744;
      background-color: #fff5f5;
      box-shadow: 0 4px 8px rgba(255, 23, 68, 0.25);
    }
    .alerta-card.advertencia {
      border-color: #ff9100;
      background-color: #fff8f0;
      box-shadow: 0 4px 8px rgba(255, 145, 0, 0.25);
    }
    .alerta-card.informacion {
      border-color: #2979ff;
      background-color: #f5f9ff;
      box-shadow: 0 4px 8px rgba(41, 121, 255, 0.25);
    }
    .alerta-card.alerta {
      border-color: #ff1744;
      background-color: #fff5f5;
      box-shadow: 0 4px 8px rgba(255, 23, 68, 0.25);
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
    .patient-info {
      margin-top: 8px;
      color: #666;
      font-size: 0.9em;
    }
    @keyframes slideIn {
      from {
        transform: translateX(-20px);
        opacity: 0;
      }
      to {
        transform: translateX(0);
        opacity: 1;
      }
    }
  `]
})
export class AlertasComponent implements OnInit, OnDestroy {
  alertas: Alerta[] = [];
  isConnected = false;
  private subscription: Subscription = new Subscription();

  constructor(private alertaWebSocketService: AlertaWebSocketService) {}

  ngOnInit() {
    this.subscription.add(
      this.alertaWebSocketService.alertas$.subscribe(alerta => {
        if (alerta) {
          this.alertas = [...this.alertas, alerta];
        }
      })
    );

    this.subscription.add(
      this.alertaWebSocketService.getConnectionStatus().subscribe(
        status => this.isConnected = status
      )
    );
  }

  ngOnDestroy() {
    this.subscription.unsubscribe();
  }
} 