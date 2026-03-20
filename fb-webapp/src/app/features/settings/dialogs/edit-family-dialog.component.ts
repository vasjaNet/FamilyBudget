import { Component, inject } from '@angular/core';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { MatDialogModule, MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import type { UpdateFamilyRequest } from '../models/family.model';

export interface EditFamilyDialogData {
  name: string;
  description: string;
}

@Component({
  selector: 'app-edit-family-dialog',
  standalone: true,
  imports: [
    ReactiveFormsModule,
    MatDialogModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
  ],
  template: `
    <h2 mat-dialog-title>Edit family</h2>
    <mat-dialog-content>
      <form [formGroup]="form">
        <mat-form-field appearance="outline" class="full-width">
          <mat-label>Name</mat-label>
          <input matInput formControlName="name" />
          @if (form.get('name')?.hasError('required') && form.get('name')?.touched) {
            <mat-error>Name is required</mat-error>
          }
        </mat-form-field>
        <mat-form-field appearance="outline" class="full-width">
          <mat-label>Description</mat-label>
          <textarea matInput formControlName="description" rows="2"></textarea>
        </mat-form-field>
      </form>
    </mat-dialog-content>
    <mat-dialog-actions align="end">
      <button mat-button mat-dialog-close>Cancel</button>
      <button mat-raised-button color="primary" [disabled]="form.invalid" (click)="submit()">Save</button>
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
export class EditFamilyDialogComponent {
  form: FormGroup;
  private readonly data = inject<EditFamilyDialogData>(MAT_DIALOG_DATA);
  private readonly dialogRef = inject(MatDialogRef<EditFamilyDialogComponent>);

  constructor(private fb: FormBuilder) {
    this.form = this.fb.nonNullable.group({
      name: [this.data?.name ?? '', [Validators.required]],
      description: [this.data?.description ?? ''],
    });
  }

  submit(): void {
    if (this.form.invalid) return;
    const value: UpdateFamilyRequest = {
      name: this.form.get('name')!.value,
      description: this.form.get('description')!.value || undefined,
    };
    this.dialogRef.close(value);
  }
}
