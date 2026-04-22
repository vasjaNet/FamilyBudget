import { Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatDialogModule, MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatButtonModule } from '@angular/material/button';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { TenantService } from '../../core/services/tenant.service';
import { Tenant, CreateTenantRequest, UpdateTenantRequest, TenantType } from '../../core/models/tenant.model';

export interface TenantFormDialogData {
  mode: 'create' | 'edit';
  tenant?: Tenant;
}

@Component({
  selector: 'app-tenant-form-dialog',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatDialogModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
    MatButtonModule,
    MatProgressSpinnerModule,
  ],
  templateUrl: './tenant-form-dialog.component.html',
  styleUrls: ['./tenant-form-dialog.component.scss'],
})
export class TenantFormDialogComponent implements OnInit {
  private fb = inject(FormBuilder);
  private dialogRef = inject(MatDialogRef<TenantFormDialogComponent>);
  private tenantService = inject(TenantService);
  data: TenantFormDialogData = inject(MAT_DIALOG_DATA);

  tenantForm!: FormGroup;
  loading = false;

  ngOnInit(): void {
    this.initForm();
  }

  private initForm(): void {
    this.tenantForm = this.fb.group({
      name: [this.data.tenant?.name || '', [Validators.required]],
      description: [this.data.tenant?.description || ''],
      type: [this.data.tenant?.type || 'PERSONAL', [Validators.required]],
    });
  }

  onSubmit(): void {
    if (this.tenantForm.invalid) return;

    this.loading = true;
    const formValue = this.tenantForm.getRawValue();

    if (this.data.mode === 'create') {
      const request: CreateTenantRequest = {
        name: formValue.name,
        description: formValue.description,
        type: formValue.type as TenantType,
      };

      this.tenantService.createTenant(request).subscribe({
        next: (response) => {
          this.loading = false;
          this.dialogRef.close(response.data);
        },
        error: () => {
          this.loading = false;
        }
      });
    } else {
      const request: UpdateTenantRequest = {
        name: formValue.name,
        description: formValue.description,
        type: formValue.type as TenantType,
      };

      this.tenantService.updateTenant(this.data.tenant!.id, request).subscribe({
        next: (response) => {
          this.loading = false;
          this.dialogRef.close(response.data);
        },
        error: () => {
          this.loading = false;
        }
      });
    }
  }

  onCancel(): void {
    this.dialogRef.close();
  }
}