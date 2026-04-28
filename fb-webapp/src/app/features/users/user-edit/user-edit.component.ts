import { Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatButtonModule } from '@angular/material/button';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatCardModule } from '@angular/material/card';
import { MatIconModule } from '@angular/material/icon';
import { MatChipsModule } from '@angular/material/chips';
import { ActivatedRoute, Router } from '@angular/router';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { UserService } from '../../../core/services/user.service';
import { UserTenantService } from '../../../core/services/user-tenant.service';
import { TenantService } from '../../../core/services/tenant.service';
import { RoleService } from '../../../core/services/role.service';
import { CreateUserRequest, UpdateUserRequest, User } from '../../../core/models/user.model';
import { UserTenantResponse } from '../../../core/models/user-tenant.model';
import { Role } from '../../../core/models/role.model';
import { Tenant } from '../../../core/models/tenant.model';
import { BreadcrumbComponent, BreadcrumbItem } from '../../../shared/components/breadcrumb/breadcrumb.component';
import { ConfirmDialogComponent, ConfirmDialogData } from '../confirm-dialog.component';
import {
  TenantAssignmentDialogComponent,
  TenantAssignmentDialogData,
  TenantAssignmentDialogResult
} from './tenant-assignment-dialog.component';

@Component({
  selector: 'app-user-edit',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
    MatButtonModule,
    MatProgressSpinnerModule,
    MatCardModule,
    MatSnackBarModule,
    MatDialogModule,
    MatIconModule,
    MatChipsModule,
    BreadcrumbComponent,
  ],
  templateUrl: './user-edit.component.html',
  styleUrls: ['./user-edit.component.scss'],
})
export class UserEditComponent implements OnInit {
  private fb = inject(FormBuilder);
  private route = inject(ActivatedRoute);
  private router = inject(Router);
  private userService = inject(UserService);
  private userTenantService = inject(UserTenantService);
  private tenantService = inject(TenantService);
  private roleService = inject(RoleService);
  private dialog = inject(MatDialog);
  private snackBar = inject(MatSnackBar);

  userForm!: FormGroup;
  loading = false;
  saving = false;
  isCreateMode = false;
  user: User | null = null;
  breadcrumbs: BreadcrumbItem[] = [];
  assignedTenants: UserTenantResponse[] = [];
  allTenants: Tenant[] = [];
  allRoles: Role[] = [];

  ngOnInit(): void {
    this.isCreateMode = this.route.snapshot.url[1].path === 'new';
    this.initForm();
    if (this.isCreateMode) {
      this.breadcrumbs = [
        { label: 'Users', url: '/users' },
        { label: 'New User' },
      ];
    } else {
      this.loadUser();
      this.loadTenantsAndRoles();
    }
  }

  private initForm(): void {
    this.userForm = this.fb.group({
      username: [{ value: '', disabled: !this.isCreateMode }, this.isCreateMode ? [Validators.required] : []],
      email: ['', [Validators.required, Validators.email]],
      firstName: [''],
      lastName: [''],
      status: ['ACTIVE', [Validators.required]],
    });
  }

  loadUser(): void {
    const id = this.route.snapshot.paramMap.get('id');
    if (!id || id === 'new') {
      this.snackBar.open('Invalid user ID', 'Close', { duration: 3000 });
      this.router.navigate(['/users']);
      return;
    }

    this.loading = true;
    this.userService.getUserById(id).subscribe({
      next: (response) => {
        this.user = response.data;
        this.assignedTenants = this.user.userTenants || [];
        this.patchForm(this.user);
        this.setBreadcrumbs(this.user);
        this.loading = false;
      },
      error: () => {
        this.snackBar.open('Failed to load user', 'Close', { duration: 3000 });
        this.loading = false;
        this.router.navigate(['/users']);
      },
    });
  }

  private patchForm(user: User): void {
    this.userForm.patchValue({
      username: user.username,
      email: user.email,
      firstName: user.firstName || '',
      lastName: user.lastName || '',
      status: user.status || 'ACTIVE',
    });
  }

  private setBreadcrumbs(user: User): void {
    this.breadcrumbs = [
      { label: 'Users', url: '/users' },
      { label: user.username },
    ];
  }

  loadTenantsAndRoles(): void {
    this.tenantService.getAllTenantsBasic().subscribe({
      next: (tResponse) => {
        this.allTenants = tResponse.data || [];
        this.roleService.getAllRolesBasic().subscribe({
          next: (rResponse) => {
            this.allRoles = rResponse.data || [];
          },
          error: () => {
            this.snackBar.open('Failed to load roles', 'Close', { duration: 3000 });
          }
        });
      },
      error: () => {
        this.snackBar.open('Failed to load tenants', 'Close', { duration: 3000 });
      }
    });
  }

  openAddTenantDialog(): void {
    const assignedTenantIds = this.assignedTenants.map(ut => ut.tenantId);
    const availableTenants = this.allTenants.filter(t => !assignedTenantIds.includes(t.id));

    if (availableTenants.length === 0) {
      this.snackBar.open('No more tenants available to assign', 'Close', { duration: 3000 });
      return;
    }

    if (this.allRoles.length === 0) {
      this.snackBar.open('No roles available', 'Close', { duration: 3000 });
      return;
    }

    const dialogRef = this.dialog.open<TenantAssignmentDialogComponent, TenantAssignmentDialogData, TenantAssignmentDialogResult | null>(
      TenantAssignmentDialogComponent,
      {
        width: '400px',
        data: {
          userId: this.user!.id,
          assignedTenantIds,
          availableTenants: availableTenants,
          availableRoles: this.allRoles
        }
      }
    );

    dialogRef.afterClosed().subscribe(selection => {
      if (selection) {
        this.userTenantService.assignUserToTenant({
          userId: this.user!.id,
          tenantId: selection.tenantId,
          roleId: selection.roleId
        }).subscribe({
          next: () => {
            this.loadUser();
            this.snackBar.open('Tenant assigned successfully', 'Close', { duration: 3000 });
          },
          error: () => {
            this.snackBar.open('Failed to assign tenant', 'Close', { duration: 3000 });
          }
        });
      }
    });
  }

  removeTenant(tenantId: string): void {
    if (!this.user) return;
    const tenantName = this.assignedTenants.find(ut => ut.tenantId === tenantId)?.tenantName || 'tenant';
    const username = this.user.username;
    const userId = this.user.id;

    const dialogRef = this.dialog.open<ConfirmDialogComponent, ConfirmDialogData, boolean>(
      ConfirmDialogComponent,
      {
        width: '400px',
        data: {
          title: 'Remove Tenant',
          message: `Are you sure you want to remove "${tenantName}" from user "${username}"?`
        }
      }
    );

    dialogRef.afterClosed().subscribe(confirmed => {
      if (confirmed) {
        this.userTenantService.removeUserFromTenant(userId, tenantId).subscribe({
          next: () => {
            this.loadUser();
            this.snackBar.open('Tenant removed successfully', 'Close', { duration: 3000 });
          },
          error: () => {
            this.snackBar.open('Failed to remove tenant', 'Close', { duration: 3000 });
          }
        });
      }
    });
  }

  onSubmit(): void {
    if (this.userForm.invalid) return;
    if (!this.isCreateMode && !this.user) return;

    this.saving = true;
    const formValue = this.userForm.getRawValue();

    if (this.isCreateMode) {
      const request: CreateUserRequest = {
        username: formValue.username,
        email: formValue.email,
        firstName: formValue.firstName,
        lastName: formValue.lastName,
        status: formValue.status,
      };
      this.userService.createUser(request).subscribe({
        next: () => {
          this.saving = false;
          this.snackBar.open('User created successfully', 'Close', { duration: 3000 });
          this.router.navigate(['/users']);
        },
        error: () => {
          this.saving = false;
          this.snackBar.open('Failed to create user', 'Close', { duration: 3000 });
        },
      });
    } else {
      const request: UpdateUserRequest = {
        email: formValue.email,
        firstName: formValue.firstName,
        lastName: formValue.lastName,
        status: formValue.status,
      };

      this.userService.updateUser(this.user!.id, request).subscribe({
        next: () => {
          this.saving = false;
          this.snackBar.open('User updated successfully', 'Close', { duration: 3000 });
          this.router.navigate(['/users']);
        },
        error: () => {
          this.saving = false;
          this.snackBar.open('Failed to update user', 'Close', { duration: 3000 });
        },
      });
    }
  }

  onCancel(): void {
    this.router.navigate(['/users']);
  }
}
