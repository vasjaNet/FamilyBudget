import { Injectable, inject } from '@angular/core';
import { Observable, of } from 'rxjs';
import { delay } from 'rxjs/operators';
import { AuthService } from '../../../core/services/auth.service';
import type { ChatMessage } from '../models/chat.model';

@Injectable({ providedIn: 'root' })
export class ChatService {
  private readonly auth = inject(AuthService);

  /** Get messages for a family/group. When backend has chat API, use it. */
  getMessages(_familyId: string): Observable<ChatMessage[]> {
    const user = this.auth.getCurrentUser();
    return of([
      {
        id: '1',
        senderId: 'other',
        senderName: 'Family Member',
        text: 'Welcome to the family chat!',
        timestamp: new Date(Date.now() - 3600000).toISOString(),
      },
      {
        id: '2',
        senderId: user?.id ?? 'me',
        senderName: user?.username ?? 'You',
        text: 'Thanks! Happy to be here.',
        timestamp: new Date().toISOString(),
      },
    ]).pipe(delay(300));
  }

  /** Send a message. When backend has chat API, use it. */
  sendMessage(_familyId: string, text: string): Observable<ChatMessage> {
    const user = this.auth.getCurrentUser();
    return of({
      id: 'stub-' + Date.now(),
      senderId: user?.id ?? 'me',
      senderName: user?.username ?? 'You',
      text,
      timestamp: new Date().toISOString(),
    }).pipe(delay(200));
  }
}
