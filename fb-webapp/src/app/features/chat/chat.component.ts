import {Component, inject} from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatCardModule } from '@angular/material/card';
import {filter, Subscription, take} from 'rxjs';
import {ChatMessage} from './models/chat.model';
import {ChatService} from './services/chat.service';
import {FormsModule} from '@angular/forms';
import {AuthService} from '../../core/services/auth.service';

@Component({
  selector: 'app-chat',
  standalone: true,
  imports: [CommonModule, MatCardModule, FormsModule],
  templateUrl: './chat.component.html',
  styleUrl: './chat.component.scss',
})
export class ChatComponent {

  rooms = ['general', 'tech', 'random'];
  currentRoom = 'general';
  messages: Record<string, ChatMessage[]> = {};
  newMessage = '';

  private readonly auth = inject(AuthService);
  username = '';

  private subs = new Subscription();

  constructor(private chatService: ChatService) {}

  ngOnInit() {
    this.chatService.connect();
    this.username = this.auth.getCurrentUser()?.username ?? '';

    // Wait for connection, then join rooms
    this.subs.add(
      this.chatService.connected$
        .pipe(filter(Boolean), take(1))
        .subscribe(() => {
          this.rooms.forEach(room => this.subscribeToRoom(room));
        })
    );
  }

  subscribeToRoom(room: string) {
    this.messages[room] = [];
    this.subs.add(
      this.chatService.joinRoom(room, this.username).subscribe(msg => {
        this.messages[room].push(msg);
      })
    );
  }

  switchRoom(room: string) {
    this.currentRoom = room;
  }

  sendMessage() {
    if (this.newMessage.trim()) {
      this.chatService.sendMessage(this.currentRoom, this.username, this.newMessage);
      this.newMessage = '';
    }
  }

  ngOnDestroy() {
    this.subs.unsubscribe();
    this.chatService.disconnect();
  }

}
