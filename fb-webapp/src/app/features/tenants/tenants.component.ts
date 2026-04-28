import { Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatTableModule } from '@angular/material/table';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { MatCardModule } from '@angular/material/card';
import { MatChipsModule } from '@angular/material/chips';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { TenantService } from '../../core/services/tenant.service';
import { UserTenantService } from '../../core/services/user-tenant.service';
import { UserService } from '../../core/services/user.service';
import { RoleService } from '../../core/services/role.service';
import { Tenant } from '../../core/models/tenant.model';
import { User } from '../../core/models/user.model';
import { Role } from '../../core/models/role.model';
import { TenantFormDialogComponent, TenantFormDialogData } from './tenant-form-dialog.component';
import { ConfirmDialogComponent, ConfirmDialogData } from '../users/confirm-dialog.component';
import {
  UserAssignmentDialogComponent,
  UserAssignmentDialogData,
  UserAssignmentDialogResult
} from './user-assignment-dialog.component';

@Component({
  selector: 'app-tenants',
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
  templateUrl: './tenants.component.html',
  styleUrls: ['./tenants.component.scss'],
})
export class TenantsComponent implements OnInit {
  private tenantService = inject(TenantService);
  private userTenantService = inject(UserTenantService);
  private userService = inject(UserService);
  private roleService = inject(RoleService);
  private dialog = inject(MatDialog);
  private snackBar = inject(MatSnackBar);

  tenants: Tenant[] = [];
  allUsers: User[] = [];
  allRoles: Role[] = [];
  loading = false;
  displayedColumns: string[] = ['name', 'description', 'type', 'users', 'createdBy', 'actions'];

  ngOnInit(): void {
    this.loadTenants();
  }

  loadTenants(): void {
    this.loading = true;
    this.tenantService.getAllTenants().subscribe({
      next: (response) => {
        this.tenants = response.data || [];
        this.loading = false;
      },
      error: (error) => {
        this.snackBar.open('Failed to load tenants', 'Close', { duration: 3000 });
        this.loading = false;
      }
    });
  }

  loadUsers(onLoaded?: () => void): void {
    this.loading = true;
    this.userService.getAllUsersBasic().subscribe({
      next: (response) => {
        this.allUsers = response.data || [];
        this.loading = false;
        onLoaded?.();
      },
      error: () => {
        this.snackBar.open('Failed to load users', 'Close', { duration: 3000 });
        this.loading = false;
      }
    });
  }

  loadRoles(onLoaded?: () => void): void {
    this.loading = true;
    this.roleService.getAllRolesBasic().subscribe({
      next: (response) => {
        this.allRoles = response.data || [];
        this.loading = false;
        onLoaded?.();
      },
      error: () => {
        this.snackBar.open('Failed to load roles', 'Close', { duration: 3000 });
        this.loading = false;
      }
    });
  }

  getUserTenantId(tenant: Tenant, username: string): string {
    const userTenant = tenant.userTenants?.find(ut => ut.userUsername === username);
    return userTenant?.id || '';
  }

  openCreateDialog(): void {
    const dialogRef = this.dialog.open<TenantFormDialogComponent, TenantFormDialogData, Tenant | null>(
      TenantFormDialogComponent,
      {
        width: '500px',
        data: { mode: 'create' }
      }
    );

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.loadTenants();
        this.snackBar.open('Tenant created successfully', 'Close', { duration: 3000 });
      }
    });
  }

  openEditDialog(tenant: Tenant): void {
    const dialogRef = this.dialog.open<TenantFormDialogComponent, TenantFormDialogData, Tenant | null>(
      TenantFormDialogComponent,
      {
        width: '500px',
        data: { mode: 'edit', tenant }
      }
    );

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.loadTenants();
        this.snackBar.open('Tenant updated successfully', 'Close', { duration: 3000 });
      }
    });
  }

  confirmDelete(tenant: Tenant): void {
    const dialogRef = this.dialog.open<ConfirmDialogComponent, ConfirmDialogData, boolean>(
      ConfirmDialogComponent,
      {
        width: '400px',
        data: {
          title: 'Delete Tenant',
          message: `Are you sure you want to delete tenant "${tenant.name}"? This action cannot be undone.`
        }
      }
    );

    dialogRef.afterClosed().subscribe(confirmed => {
      if (confirmed) {
        this.deleteTenant(tenant.id);
      }
    });
  }

  deleteTenant(id: string): void {
    this.tenantService.deleteTenant(id).subscribe({
      next: () => {
        this.loadTenants();
        this.snackBar.open('Tenant deleted successfully', 'Close', { duration: 3000 });
      },
      error: () => {
        this.snackBar.open('Failed to delete tenant', 'Close', { duration: 3000 });
      }
    });
  }

  getTypeClass(type: string): string {
    switch (type) {
      case 'PERSONAL': return 'type-personal';
      case 'FAMILY': return 'type-family';
      case 'BUSINESS': return 'type-business';
      default: return '';
    }
  }

  openUserAssignmentDialog(tenant: Tenant): void {
    if (!this.allUsers.length) {
      this.loadUsers(() => this.openUserAssignmentDialog(tenant));
      return;
    }
    if (!this.allRoles.length) {
      this.loadRoles(() => this.openUserAssignmentDialog(tenant));
      return;
    }

    const assignedUserIds = tenant.userTenants?.map(ut => ut.userId) || [];

    const dialogRef = this.dialog.open<UserAssignmentDialogComponent, UserAssignmentDialogData, UserAssignmentDialogResult | null>(
      UserAssignmentDialogComponent,
      {
        width: '400px',
        data: {
          tenantId: tenant.id,
          assignedUserIds,
          availableUsers: this.allUsers,
          availableRoles: this.allRoles
        }
      }
    );

    dialogRef.afterClosed().subscribe(selection => {
      if (selection) {
        const selectedUser = this.allUsers.find(u => u.id === selection.userId);
        this.userTenantService.assignUserToTenant({
          userId: selection.userId,
          tenantId: tenant.id,
          roleId: selection.roleId
        }).subscribe({
          next: () => {
            this.loadTenants();
            this.snackBar.open(`User ${selectedUser?.username || 'user'} assigned to tenant successfully`, 'Close', { duration: 3000 });
          },
          error: () => {
            this.snackBar.open('Failed to assign user to tenant', 'Close', { duration: 3000 });
          }
        });
      }
    });
  }

  removeUserFromTenant(tenant: Tenant, userTenantId: string): void {
    const userName = tenant.userTenants?.find(ut => ut.id === userTenantId)?.userUsername || 'user';
    const dialogRef = this.dialog.open<ConfirmDialogComponent, ConfirmDialogData, boolean>(
      ConfirmDialogComponent,
      {
        width: '400px',
        data: {
          title: 'Remove User',
          message: `Are you sure you want to remove "${userName}" from tenant "${tenant.name}"?`
        }
      }
    );

    dialogRef.afterClosed().subscribe(confirmed => {
      if (confirmed) {
        /*this.userTenantService.removeUserFromTenantById(userTenantId).subscribe({
          next: () => {
            this.loadTenants();
            this.snackBar.open('User removed from tenant successfully', 'Close', { duration: 3000 });
          },
          error: () => {
            this.snackBar.open('Failed to remove user from tenant', 'Close', { duration: 3000 });
          }
        });*/
      }
    });
  }
}
