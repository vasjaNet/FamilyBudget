import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { MatCardModule } from '@angular/material/card';
import { MatListModule } from '@angular/material/list';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatSelectModule } from '@angular/material/select';
import { RouterLink } from '@angular/router';
import { ChatService } from './services/chat.service';
import { FamilyService } from '../settings/services/family.service';
import { AuthService } from '../../core/services/auth.service';
import type { ChatMessage } from './models/chat.model';
import type { Family } from '../settings/models/family.model';

@Component({
  selector: 'app-chat',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    MatCardModule,
    MatListModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatSelectModule,
    RouterLink,
  ],
  templateUrl: './chat.component.html',
  styleUrl: './chat.component.scss',
})
export class ChatComponent implements OnInit {
  private readonly chatService = inject(ChatService);
  private readonly familyService = inject(FamilyService);
  private readonly auth = inject(AuthService);

  families = signal<Family[]>([]);
  selectedFamilyId = signal<string | null>(null);
  messages = signal<ChatMessage[]>([]);
  newMessage = '';
  sending = false;

  ngOnInit(): void {
    this.familyService.getMyFamilies().subscribe((list) => {
      this.families.set(list);
      if (list.length > 0 && !this.selectedFamilyId()) {
        this.selectFamily(list[0].id);
      }
    });
  }

  selectFamily(id: string): void {
    this.selectedFamilyId.set(id);
    this.messages.set([]);
    this.chatService.getMessages(id).subscribe((m) => this.messages.set(m));
  }

  send(): void {
    const familyId = this.selectedFamilyId();
    const text = this.newMessage?.trim();
    if (!familyId || !text) return;
    this.sending = true;
    this.chatService.sendMessage(familyId, text).subscribe({
      next: (msg) => {
        this.messages.update((list) => [...list, msg]);
        this.newMessage = '';
      },
      complete: () => (this.sending = false),
    });
  }

  isOwnMessage(msg: ChatMessage): boolean {
    return msg.senderId === this.auth.getCurrentUser()?.id;
  }
}
