import { Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatDialogModule, MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatButtonModule } from '@angular/material/button';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { UserService } from '../../core/services/user.service';
import { CreateUserRequest, UpdateUserRequest, User } from '../../core/models/user.model';

export interface UserFormDialogData {
  mode: 'create' | 'edit';
  user?: User;
}

@Component({
  selector: 'app-user-form-dialog',
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
  template: `
    <h2 mat-dialog-title>{{ data.mode === 'create' ? 'Create User' : 'Edit User' }}</h2>
    <form [formGroup]="userForm" (ngSubmit)="onSubmit()">
      <mat-dialog-content>
        <mat-form-field appearance="outline" class="full-width">
          <mat-label>Username</mat-label>
          <input matInput formControlName="username" placeholder="Enter username">
          <mat-error *ngIf="userForm.get('username')?.hasError('required')">
            Username is required
          </mat-error>
        </mat-form-field>

        <mat-form-field appearance="outline" class="full-width">
          <mat-label>Email</mat-label>
          <input matInput formControlName="email" type="email" placeholder="Enter email">
          <mat-error *ngIf="userForm.get('email')?.hasError('required')">
            Email is required
          </mat-error>
          <mat-error *ngIf="userForm.get('email')?.hasError('email')">
            Please enter a valid email
          </mat-error>
        </mat-form-field>

        <mat-form-field appearance="outline" class="full-width">
          <mat-label>First Name</mat-label>
          <input matInput formControlName="firstName" placeholder="Enter first name">
        </mat-form-field>

        <mat-form-field appearance="outline" class="full-width">
          <mat-label>Last Name</mat-label>
          <input matInput formControlName="lastName" placeholder="Enter last name">
        </mat-form-field>

        <mat-form-field appearance="outline" class="full-width">
          <mat-label>Status</mat-label>
          <mat-select formControlName="status">
            <mat-option value="ACTIVE">Active</mat-option>
            <mat-option value="DISABLED">Disabled</mat-option>
            <mat-option value="PASSWORD_EXPIRED">Password Expired</mat-option>
          </mat-select>
          <mat-error *ngIf="userForm.get('status')?.hasError('required')">
            Status is required
          </mat-error>
        </mat-form-field>
      </mat-dialog-content>

      <mat-dialog-actions align="end">
        <button mat-button type="button" (click)="onCancel()">Cancel</button>
        <button mat-raised-button color="primary" type="submit" [disabled]="userForm.invalid || loading">
          <mat-spinner *ngIf="loading" diameter="20" class="spinner"></mat-spinner>
          <span *ngIf="!loading">{{ data.mode === 'create' ? 'Create' : 'Update' }}</span>
        </button>
      </mat-dialog-actions>
    </form>
  `,
  styles: [`
    .full-width {
      width: 100%;
      margin-bottom: 16px;
    }

    mat-dialog-content {
      min-width: 400px;
    }

    .spinner {
      display: inline-block;
      margin-right: 8px;
    }
  `]
})
export class UserFormDialogComponent implements OnInit {
  private fb = inject(FormBuilder);
  private dialogRef = inject(MatDialogRef<UserFormDialogComponent>);
  private userService = inject(UserService);
  data: UserFormDialogData = inject(MAT_DIALOG_DATA);

  userForm!: FormGroup;
  loading = false;

  ngOnInit(): void {
    this.initForm();
  }

  private initForm(): void {
    this.userForm = this.fb.group({
      username: [
        { value: this.data.user?.username || '', disabled: this.data.mode === 'edit' },
        [Validators.required]
      ],
      email: [this.data.user?.email || '', [Validators.required, Validators.email]],
      firstName: [this.data.user?.firstName || ''],
      lastName: [this.data.user?.lastName || ''],
      status: [this.data.user?.status || 'ACTIVE', [Validators.required]],
    });
  }

  onSubmit(): void {
    if (this.userForm.invalid) return;

    this.loading = true;
    const formValue = this.userForm.getRawValue();

    if (this.data.mode === 'create') {
      const request: CreateUserRequest = {
        username: formValue.username,
        email: formValue.email,
        firstName: formValue.firstName,
        lastName: formValue.lastName,
        status: formValue.status,
      };

      this.userService.createUser(request).subscribe({
        next: (response) => {
          this.loading = false;
          this.dialogRef.close(response.data);
        },
        error: () => {
          this.loading = false;
        }
      });
    } else {
      const request: UpdateUserRequest = {
        email: formValue.email,
        firstName: formValue.firstName,
        lastName: formValue.lastName,
        status: formValue.status,
      };

      this.userService.updateUser(this.data.user!.id, request).subscribe({
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
