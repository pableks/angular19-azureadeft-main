import { Injectable } from '@angular/core';
import { Client } from '@stomp/stompjs';
import { BehaviorSubject } from 'rxjs';
import SockJS from 'sockjs-client';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class AlertaWebSocketService {
  private client: Client;
  private alertasSubject = new BehaviorSubject<any>(null);
  private connectionStatusSubject = new BehaviorSubject<boolean>(false);
  alertas$ = this.alertasSubject.asObservable();

  constructor() {
    this.client = new Client({
      webSocketFactory: () => new SockJS('http://localhost:8085/ws-alertas'),
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
  }

  private subscribe(): void {
    this.client.subscribe('/topic/alertas', message => {
      console.log('Received WebSocket message:', message.body);
      const alerta = JSON.parse(message.body);
      this.alertasSubject.next(alerta);
    });
  }

  public getConnectionStatus(): Observable<boolean> {
    return this.connectionStatusSubject.asObservable();
  }
}