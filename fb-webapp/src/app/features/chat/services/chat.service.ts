import { Injectable } from '@angular/core';
import { Client, IMessage, StompSubscription } from '@stomp/stompjs';
import SockJS from 'sockjs-client';
import { Subject, BehaviorSubject } from 'rxjs';
import {ChatMessage} from '../models/chat.model';


@Injectable({ providedIn: 'root' })
export class ChatService {
  private client: Client;
  private subscriptions = new Map<string, StompSubscription>();

  // Emits messages per room
  private messageSubjects = new Map<string, Subject<ChatMessage>>();
  public connected$ = new BehaviorSubject<boolean>(false);

  constructor() {
    this.client = new Client({
      webSocketFactory: () => new SockJS('http://localhost:8081/chat'),
      reconnectDelay: 5000,
      onConnect: () => {
        this.connected$.next(true);
        console.log('Connected to WebSocket');
      },
      onDisconnect: () => {
        this.connected$.next(false);
      }
    });
  }

  connect(): void {
    this.client.activate();
  }

  disconnect(): void {
    this.client.deactivate();
  }

  /** Subscribe to a room and get an Observable of its messages */
  joinRoom(room: string, username: string) {
    if (!this.messageSubjects.has(room)) {
      this.messageSubjects.set(room, new Subject<ChatMessage>());
    }

    // Subscribe to room topic
    const sub = this.client.subscribe(
      `/topic/room/${room}`,
      (message: IMessage) => {
        const msg: ChatMessage = JSON.parse(message.body);
        this.messageSubjects.get(room)!.next(msg);
      }
    );
    this.subscriptions.set(room, sub);

    // Notify others that user joined
    this.client.publish({
      destination: `/app/chat.join/${room}`,
      body: JSON.stringify({ sender: username, room })
    });

    return this.messageSubjects.get(room)!.asObservable();
  }

  leaveRoom(room: string): void {
    this.subscriptions.get(room)?.unsubscribe();
    this.subscriptions.delete(room);
  }

  sendMessage(room: string, sender: string, content: string): void {
    this.client.publish({
      destination: `/app/chat.sendMessage/${room}`,
      body: JSON.stringify({ sender, content, room, type: 'CHAT' })
    });
  }
}
