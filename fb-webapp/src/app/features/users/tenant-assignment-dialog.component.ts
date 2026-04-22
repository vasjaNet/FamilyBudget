import { Component, Inject, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { MAT_DIALOG_DATA, MatDialogRef, MatDialogModule } from '@angular/material/dialog';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatSelectModule } from '@angular/material/select';
import { MatOptionModule } from '@angular/material/core';
import { Tenant } from '../../core/models/tenant.model';

export interface TenantAssignmentDialogData {
  userId: string;
  assignedTenantIds: string[];
  availableTenants: Tenant[];
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
    </mat-dialog-content>
    <mat-dialog-actions align="end">
      <button mat-button (click)="onCancel()">Cancel</button>
      <button mat-raised-button color="primary" [disabled]="!selectedTenantId" (click)="onConfirm()">
        Add
      </button>
    </mat-dialog-actions>
  `,
})
export class TenantAssignmentDialogComponent {
  private dialogRef = inject(MatDialogRef<TenantAssignmentDialogComponent, string | null>);

  selectedTenantId: string | null = null;

  constructor(@Inject(MAT_DIALOG_DATA) public data: TenantAssignmentDialogData) {}

  get availableTenants(): Tenant[] {
    return this.data.availableTenants.filter(
      t => !this.data.assignedTenantIds.includes(t.id)
    );
  }

  onConfirm(): void {
    if (this.selectedTenantId) {
      this.dialogRef.close(this.selectedTenantId);
    }
  }

  onCancel(): void {
    this.dialogRef.close(null);
  }
}