import { Component } from '@angular/core';
import { RouterLink } from '@angular/router';

@Component({
    selector: 'app-unauthorized',
    template: `
    <div class="unauthorized-container">
      <h2>Acceso No Autorizado</h2>
      <p>No tienes los permisos necesarios para acceder a esta página.</p>
      <button mat-raised-button color="primary" routerLink="/">Volver al Inicio</button>
    </div>
  `,
    styles: [`
    .unauthorized-container {
      padding: 20px;
      text-align: center;
    }
  `],
    imports: [RouterLink]
})
export class UnauthorizedComponent {} 