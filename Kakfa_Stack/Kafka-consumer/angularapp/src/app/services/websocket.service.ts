import { Injectable } from '@angular/core';
import { Client, Message } from '@stomp/stompjs';
import { BehaviorSubject, Observable } from 'rxjs';
import { SignosVitales } from '../models/signos-vitales.model';

@Injectable({
  providedIn: 'root'
})
export class WebSocketService {
  private client: Client;
  private signosVitalesSubject = new BehaviorSubject<SignosVitales[]>([]);

  constructor() {
    this.client = new Client({
      brokerURL: 'ws://localhost:9096/ws-signos-vitales',
      debug: function (str) {
        console.log(str);
      },
      reconnectDelay: 5000,
      heartbeatIncoming: 4000,
      heartbeatOutgoing: 4000
    });

    this.client.onConnect = () => {
      console.log('Connected to WebSocket');
      this.subscribe();
    };

    this.client.onStompError = (frame) => {
      console.error('WebSocket error:', frame);
    };

    this.client.activate();
  }

  private subscribe(): void {
    console.log('Attempting to subscribe to /topic/signosvitales');
    this.client.subscribe('/topic/signosvitales', (message: Message) => {
      console.log('Received WebSocket message:', message.body);
      const signosVitales: SignosVitales = JSON.parse(message.body);
      const currentData = this.signosVitalesSubject.value;
      console.log('Current data length:', currentData.length);
      const updatedData = [...currentData, signosVitales].slice(-5);
      this.signosVitalesSubject.next(updatedData);
    });
  }

  public getSignosVitales(): Observable<SignosVitales[]> {
    return this.signosVitalesSubject.asObservable();
  }
} 