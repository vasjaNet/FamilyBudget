import { Component } from '@angular/core';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatButtonModule } from '@angular/material/button';
import type { AddMemberRequest, FamilyRole } from '../models/family.model';

const ROLES: FamilyRole[] = ['OWNER', 'FULL_ACCESS', 'INFO_ONLY'];

@Component({
  selector: 'app-add-member-dialog',
  standalone: true,
  imports: [
    ReactiveFormsModule,
    MatDialogModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
    MatButtonModule,
  ],
  template: `
    <h2 mat-dialog-title>Add member</h2>
    <mat-dialog-content>
      <form [formGroup]="form">
        <mat-form-field appearance="outline" class="full-width">
          <mat-label>User ID or email</mat-label>
          <input matInput formControlName="userId" placeholder="UUID or email to invite" />
          @if (form.get('userId')?.hasError('required') && form.get('userId')?.touched) {
            <mat-error>User ID or email is required</mat-error>
          }
        </mat-form-field>
        <mat-form-field appearance="outline" class="full-width">
          <mat-label>Role</mat-label>
          <mat-select formControlName="role">
            @for (r of roles; track r) {
              <mat-option [value]="r">{{ r }}</mat-option>
            }
          </mat-select>
        </mat-form-field>
      </form>
    </mat-dialog-content>
    <mat-dialog-actions align="end">
      <button mat-button mat-dialog-close>Cancel</button>
      <button mat-raised-button color="primary" [disabled]="form.invalid" (click)="submit()">Add</button>
    </mat-dialog-actions>
  `,
  styles: [
    `
      .full-width {
        width: 100%;
        display: block;
      }
      mat-dialog-content {
        min-width: 320px;
      }
    `,
  ],
})
export class AddMemberDialogComponent {
  form: FormGroup;
  roles = ROLES;

  constructor(
    private fb: FormBuilder,
    private dialogRef: MatDialogRef<AddMemberDialogComponent>
  ) {
    this.form = this.fb.nonNullable.group({
      userId: ['', [Validators.required]],
      role: ['FULL_ACCESS' as FamilyRole, [Validators.required]],
    });
  }

  submit(): void {
    if (this.form.invalid) return;
    const value: AddMemberRequest = this.form.getRawValue();
    this.dialogRef.close(value);
  }
}
