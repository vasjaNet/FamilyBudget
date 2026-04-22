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
  templateUrl: './user-form-dialog.component.html',
  styleUrls: ['./user-form-dialog.component.scss'],
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