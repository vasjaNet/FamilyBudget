import { Component, Inject, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { MAT_DIALOG_DATA, MatDialogRef, MatDialogModule } from '@angular/material/dialog';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatSelectModule } from '@angular/material/select';
import { MatOptionModule } from '@angular/material/core';
import { Tenant } from '../../../core/models/tenant.model';
import { Role } from '../../../core/models/role.model';

export interface TenantAssignmentDialogData {
  userId: string;
  assignedTenantIds: string[];
  availableTenants: Tenant[];
  availableRoles: Role[];
}

export interface TenantAssignmentDialogResult {
  tenantId: string;
  roleId: string;
}

@Component({
  selector: 'app-tenant-assignment-dialog',
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
    <h2 mat-dialog-title>Add Tenant</h2>
    <mat-dialog-content>
      <mat-form-field appearance="outline" style="width: 100%;">
        <mat-label>Select Tenant</mat-label>
        <mat-select [(value)]="selectedTenantId">
          @for (tenant of availableTenants; track tenant.id) {
            <mat-option [value]="tenant.id">{{ tenant.name }}</mat-option>
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
      <button mat-raised-button color="primary" [disabled]="!selectedTenantId || !selectedRoleId" (click)="onConfirm()">
        Add
      </button>
    </mat-dialog-actions>
  `,
})
export class TenantAssignmentDialogComponent {
  private dialogRef = inject(MatDialogRef<TenantAssignmentDialogComponent, TenantAssignmentDialogResult | null>);

  selectedTenantId: string | null = null;
  selectedRoleId: string | null = null;

  constructor(@Inject(MAT_DIALOG_DATA) public data: TenantAssignmentDialogData) {}

  get availableTenants(): Tenant[] {
    return this.data.availableTenants.filter(
      t => !this.data.assignedTenantIds.includes(t.id)
    );
  }

  onConfirm(): void {
    if (this.selectedTenantId && this.selectedRoleId) {
      this.dialogRef.close({
        tenantId: this.selectedTenantId,
        roleId: this.selectedRoleId
      });
    }
  }

  onCancel(): void {
    this.dialogRef.close(null);
  }
}
