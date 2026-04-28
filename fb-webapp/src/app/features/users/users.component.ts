import { Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatTableModule } from '@angular/material/table';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { MatCardModule } from '@angular/material/card';
import { MatChipsModule } from '@angular/material/chips';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { RouterLink } from '@angular/router';
import { UserService } from '../../core/services/user.service';
import { User } from '../../core/models/user.model';
import { UserFormDialogComponent, UserFormDialogData } from './user-form-dialog.component';
import { ConfirmDialogComponent, ConfirmDialogData } from './confirm-dialog.component';

@Component({
  selector: 'app-users',
  standalone: true,
  imports: [
    CommonModule,
    MatTableModule,
    MatButtonModule,
    MatIconModule,
    MatDialogModule,
    MatCardModule,
    MatChipsModule,
    MatSnackBarModule,
    RouterLink,
  ],
  templateUrl: './users.component.html',
  styleUrls: ['./users.component.scss'],
})
export class UsersComponent implements OnInit {
  private userService = inject(UserService);
  private dialog = inject(MatDialog);
  private snackBar = inject(MatSnackBar);

  users: User[] = [];
  loading = false;
  displayedColumns: string[] = ['username', 'email', 'firstName', 'lastName', 'tenants', 'status', 'actions'];

  ngOnInit(): void {
    this.loadUsers();
  }

  loadUsers(): void {
    this.loading = true;
    this.userService.getAllUsers().subscribe({
      next: (userResponse) => {
        this.users = userResponse.data || [];
        this.loading = false;
      },
      error: (error) => {
        this.snackBar.open('Failed to load users', 'Close', { duration: 3000 });
        this.loading = false;
      }
    });
  }

  openCreateDialog(): void {
    const dialogRef = this.dialog.open<UserFormDialogComponent, UserFormDialogData, User | null>(
      UserFormDialogComponent,
      {
        width: '500px',
        data: { mode: 'create' }
      }
    );

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.loadUsers();
        this.snackBar.open('User created successfully', 'Close', { duration: 3000 });
      }
    });
  }

  confirmDelete(user: User): void {
    const dialogRef = this.dialog.open<ConfirmDialogComponent, ConfirmDialogData, boolean>(
      ConfirmDialogComponent,
      {
        width: '400px',
        data: {
          title: 'Delete User',
          message: `Are you sure you want to delete user "${user.username}"? This action cannot be undone.`
        }
      }
    );

    dialogRef.afterClosed().subscribe(confirmed => {
      if (confirmed) {
        this.deleteUser(user.id);
      }
    });
  }

  deleteUser(id: string): void {
    this.userService.deleteUser(id).subscribe({
      next: () => {
        this.loadUsers();
        this.snackBar.open('User deleted successfully', 'Close', { duration: 3000 });
      },
      error: () => {
        this.snackBar.open('Failed to delete user', 'Close', { duration: 3000 });
      }
    });
  }

  getStatusClass(status: string): string {
    switch (status) {
      case 'ACTIVE': return 'status-active';
      case 'DISABLED': return 'status-disabled';
      case 'PASSWORD_EXPIRED': return 'status-password-expired';
      default: return '';
    }
  }
}