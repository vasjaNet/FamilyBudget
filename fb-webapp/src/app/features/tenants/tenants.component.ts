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
import { Tenant } from '../../core/models/tenant.model';
import { TenantFormDialogComponent, TenantFormDialogData } from './tenant-form-dialog.component';
import { ConfirmDialogComponent, ConfirmDialogData } from '../users/confirm-dialog.component';

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
  private dialog = inject(MatDialog);
  private snackBar = inject(MatSnackBar);

  tenants: Tenant[] = [];
  loading = false;
  displayedColumns: string[] = ['name', 'description', 'type', 'createdBy', 'actions'];

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
}