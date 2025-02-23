import { Injectable } from '@angular/core';
import { Client } from '@stomp/stompjs';
import SockJS from 'sockjs-client';
import { BehaviorSubject, Observable } from 'rxjs';
import { SignosVitales } from '../models/signos-vitales.model';

@Injectable({
  providedIn: 'root'
})
export class WebSocketService {
  private client: Client;
  private signosVitalesSubject = new BehaviorSubject<SignosVitales[]>([]);
  private connectionStatusSubject = new BehaviorSubject<boolean>(false);

  constructor() {
    this.client = new Client({
      webSocketFactory: () => new SockJS('http://localhost:9096/ws-signos-vitales'),
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
      this.connectionStatusSubject.next(true);
    };

    this.client.onDisconnect = () => {
      console.log('Disconnected from WebSocket');
      this.connectionStatusSubject.next(false);
    };

    this.client.activate();

    // Clean old data every minute
    setInterval(() => this.cleanOldData(), 60000);
  }

  private subscribe(): void {
    this.client.subscribe('/topic/signos-vitales', (message) => {
      console.log('Received WebSocket message:', message.body);
      const signosVitales: SignosVitales = JSON.parse(message.body);
      
      // Always set a new timestamp when receiving data
      signosVitales.timestamp = new Date().toISOString();
      
      const currentData = this.signosVitalesSubject.value;
      const updatedData = [...currentData, signosVitales];
      this.signosVitalesSubject.next(updatedData);
    });
  }

  private cleanOldData(): void {
    const maxAge = 30 * 60 * 1000; // 30 minutes in milliseconds
    const now = new Date().getTime();
    
    const currentData = this.signosVitalesSubject.value;
    const cleanedData = currentData.filter(data => {
      const dataTime = new Date(data.timestamp).getTime();
      return (now - dataTime) <= maxAge;
    });
    
    if (cleanedData.length !== currentData.length) {
      this.signosVitalesSubject.next(cleanedData);
    }
  }

  public getSignosVitales(): Observable<SignosVitales[]> {
    return this.signosVitalesSubject.asObservable();
  }

  public getConnectionStatus(): Observable<boolean> {
    return this.connectionStatusSubject.asObservable();
  }
}