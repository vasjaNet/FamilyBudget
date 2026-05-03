/*export interface ChatMessage {
  id: string;
  senderId: string;
  senderName: string;
  text: string;
  timestamp: string;
}*/
export interface ChatMessage {
  sender: string;
  content: string;
  room: string;
  timestamp: string;
  type: 'CHAT' | 'JOIN' | 'LEAVE';
}
