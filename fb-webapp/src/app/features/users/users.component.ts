import { Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatTableModule } from '@angular/material/table';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { MatCardModule } from '@angular/material/card';
import { MatChipsModule } from '@angular/material/chips';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { UserService } from '../../core/services/user.service';
import { UserTenantService } from '../../core/services/user-tenant.service';
import { TenantService } from '../../core/services/tenant.service';
import { RoleService } from '../../core/services/role.service';
import { User } from '../../core/models/user.model';
import { Tenant } from '../../core/models/tenant.model';
import { Role } from '../../core/models/role.model';
import { UserFormDialogComponent, UserFormDialogData } from './user-form-dialog.component';
import { ConfirmDialogComponent, ConfirmDialogData } from './confirm-dialog.component';
import {
  TenantAssignmentDialogComponent,
  TenantAssignmentDialogData,
  TenantAssignmentDialogResult
} from './tenant-assignment-dialog.component';

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
  ],
  templateUrl: './users.component.html',
  styleUrls: ['./users.component.scss'],
})
export class UsersComponent implements OnInit {
  private userService = inject(UserService);
  private userTenantService = inject(UserTenantService);
  private tenantService = inject(TenantService);
  private roleService = inject(RoleService);
  private dialog = inject(MatDialog);
  private snackBar = inject(MatSnackBar);

  users: User[] = [];
  allTenants: Tenant[] = [];
  allRoles: Role[] = [];
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

      },
      error: (error) => {
        this.snackBar.open('Failed to load users', 'Close', { duration: 3000 });
        this.loading = false;
      }
    });

  }

  loadTenants(onLoaded?: () => void): void {
    this.loading = true;
    this.tenantService.getAllTenants().subscribe({
      next: (tResponse) => {
        this.allTenants = tResponse.data || [];
        this.loading = false;
        onLoaded?.();
      },
      error: () => {
        this.snackBar.open('Failed to load tenants', 'Close', { duration: 3000 });
        this.loading = false;
      }
    });
  }

  loadRoles(onLoaded?: () => void): void {
    this.loading = true;
    this.roleService.getAllRoles().subscribe({
      next: (rResponse) => {
        this.allRoles = rResponse.data || [];
        this.loading = false;
        onLoaded?.();
      },
      error: () => {
        this.snackBar.open('Failed to load roles', 'Close', { duration: 3000 });
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

  openEditDialog(user: User): void {
    const dialogRef = this.dialog.open<UserFormDialogComponent, UserFormDialogData, User | null>(
      UserFormDialogComponent,
      {
        width: '500px',
        data: { mode: 'edit', user }
      }
    );

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.loadUsers();
        this.snackBar.open('User updated successfully', 'Close', { duration: 3000 });
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

  getUserTenantId(user: User, tenantName: string): string {
    const userTenant = user.userTenants?.find(ut => ut.tenantName === tenantName);
    return userTenant?.tenantId || '';
  }

  openTenantAssignmentDialog(user: User): void {
    if (!this.allTenants.length) {
      this.loadTenants(() => this.openTenantAssignmentDialog(user));
      return;
    }
    if (!this.allRoles.length) {
      this.loadRoles(() => this.openTenantAssignmentDialog(user));
      return;
    }

    const assignedTenantIds = user.userTenants?.map(ut => ut.tenantId) || [];

    const dialogRef = this.dialog.open<TenantAssignmentDialogComponent, TenantAssignmentDialogData, TenantAssignmentDialogResult | null>(
      TenantAssignmentDialogComponent,
      {
        width: '400px',
        data: {
          userId: user.id,
          assignedTenantIds,
          availableTenants: this.allTenants,
          availableRoles: this.allRoles
        }
      }
    );

    dialogRef.afterClosed().subscribe(selection => {
      if (selection) {
        const selectedTenant = this.allTenants.find(t => t.id === selection.tenantId);
        this.userTenantService.assignUserToTenant({
          userId: user.id,
          tenantId: selection.tenantId,
          roleId: selection.roleId
        }).subscribe({
          next: () => {
            this.loadUsers();
            this.snackBar.open(`User assigned to ${selectedTenant?.name || 'tenant'} successfully`, 'Close', { duration: 3000 });
          },
          error: () => {
            this.snackBar.open('Failed to assign user to tenant', 'Close', { duration: 3000 });
          }
        });
      }
    });
  }

  removeTenantFromUser(user: User, tenantId: string): void {
    const tenantName = user.userTenants?.find(ut => ut.tenantId === tenantId)?.tenantName || 'tenant';
    const dialogRef = this.dialog.open<ConfirmDialogComponent, ConfirmDialogData, boolean>(
      ConfirmDialogComponent,
      {
        width: '400px',
        data: {
          title: 'Remove Tenant',
          message: `Are you sure you want to remove "${tenantName}" from user "${user.username}"?`
        }
      }
    );

    dialogRef.afterClosed().subscribe(confirmed => {
      if (confirmed) {
        this.userTenantService.removeUserFromTenant(user.id, tenantId).subscribe({
          next: () => {
            this.loadUsers();
            this.snackBar.open('User removed from tenant successfully', 'Close', { duration: 3000 });
          },
          error: () => {
            this.snackBar.open('Failed to remove user from tenant', 'Close', { duration: 3000 });
          }
        });
      }
    });
  }
}
