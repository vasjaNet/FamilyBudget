import { Component, Inject, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { MAT_DIALOG_DATA, MatDialogRef, MatDialogModule } from '@angular/material/dialog';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatSelectModule } from '@angular/material/select';
import { MatOptionModule } from '@angular/material/core';
import { User } from '../../core/models/user.model';
import { Role } from '../../core/models/role.model';

export interface UserAssignmentDialogData {
  tenantId: string;
  assignedUserIds: string[];
  availableUsers: User[];
  availableRoles: Role[];
}

export interface UserAssignmentDialogResult {
  userId: string;
  roleId: string;
}

@Component({
  selector: 'app-user-assignment-dialog',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    MatDialogModule,
    MatButtonModule,
    MatFormFieldModule,
    MatSelectModule,
    MatOptionModule,
  ],
  template: `
    <h2 mat-dialog-title>Add User</h2>
    <mat-dialog-content>
      <mat-form-field appearance="outline" style="width: 100%;">
        <mat-label>Select User</mat-label>
        <mat-select [(value)]="selectedUserId">
          @for (user of availableUsers; track user.id) {
            <mat-option [value]="user.id">{{ user.username }} ({{ user.email }})</mat-option>
          }
        </mat-select>
      </mat-form-field>

      <mat-form-field appearance="outline" style="width: 100%;">
        <mat-label>Select Role</mat-label>
        <mat-select [(value)]="selectedRoleId">
          @for (role of data.availableRoles; track role.id) {
            <mat-option [value]="role.id">{{ role.name }}</mat-option>
          }
        </mat-select>
      </mat-form-field>
    </mat-dialog-content>
    <mat-dialog-actions align="end">
      <button mat-button (click)="onCancel()">Cancel</button>
      <button mat-raised-button color="primary" [disabled]="!selectedUserId || !selectedRoleId" (click)="onConfirm()">
        Add
      </button>
    </mat-dialog-actions>
  `,
})
export class UserAssignmentDialogComponent {
  private dialogRef = inject(MatDialogRef<UserAssignmentDialogComponent, UserAssignmentDialogResult | null>);

  selectedUserId: string | null = null;
  selectedRoleId: string | null = null;

  constructor(@Inject(MAT_DIALOG_DATA) public data: UserAssignmentDialogData) {}

  get availableUsers(): User[] {
    return this.data.availableUsers.filter(
      u => !this.data.assignedUserIds.includes(u.id)
    );
  }

  onConfirm(): void {
    if (this.selectedUserId && this.selectedRoleId) {
      this.dialogRef.close({
        userId: this.selectedUserId,
        roleId: this.selectedRoleId
      });
    }
  }

  onCancel(): void {
    this.dialogRef.close(null);
  }
}