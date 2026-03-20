import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatListModule } from '@angular/material/list';
import { MatIconModule } from '@angular/material/icon';
import { MatDialogModule, MatDialog } from '@angular/material/dialog';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { MatChipsModule } from '@angular/material/chips';
import { FamilyService } from './services/family.service';
import type { Family, FamilyMember } from './models/family.model';
import { CreateFamilyDialogComponent } from './dialogs/create-family-dialog.component';
import { EditFamilyDialogComponent } from './dialogs/edit-family-dialog.component';
import { AddMemberDialogComponent } from './dialogs/add-member-dialog.component';

@Component({
  selector: 'app-settings',
  standalone: true,
  imports: [
    CommonModule,
    MatCardModule,
    MatButtonModule,
    MatListModule,
    MatIconModule,
    MatDialogModule,
    MatSnackBarModule,
    MatChipsModule,
  ],
  templateUrl: './settings.component.html',
  styleUrl: './settings.component.scss',
})
export class SettingsComponent implements OnInit {
  private readonly familyService = inject(FamilyService);
  private readonly dialog = inject(MatDialog);
  private readonly snackBar = inject(MatSnackBar);

  families: Family[] = [];
  selectedFamily: Family | null = null;
  members: FamilyMember[] = [];
  loading = false;

  ngOnInit(): void {
    this.loadFamilies();
  }

  loadFamilies(): void {
    this.loading = true;
    this.familyService.getMyFamilies().subscribe({
      next: (list) => {
        this.families = list;
        if (list.length > 0 && !this.selectedFamily) {
          this.selectFamily(list[0]);
        } else if (!list.some((f) => f.id === this.selectedFamily?.id)) {
          this.selectedFamily = null;
          this.members = [];
        }
        this.loading = false;
      },
      error: () => (this.loading = false),
    });
  }

  selectFamily(family: Family): void {
    this.selectedFamily = family;
    this.members = [];
    this.familyService.getMembers(family.id).subscribe((m) => (this.members = m));
  }

  openCreateFamily(): void {
    const ref = this.dialog.open(CreateFamilyDialogComponent, { width: '400px' });
    ref.afterClosed().subscribe((result) => {
      if (result) {
        this.familyService.createFamily(result).subscribe({
          next: (created) => {
            this.families = [...this.families, created];
            this.snackBar.open('Family created', 'Close', { duration: 3000 });
          },
          error: () => this.snackBar.open('Failed to create family', 'Close', { duration: 5000 }),
        });
      }
    });
  }

  openEditFamily(): void {
    if (!this.selectedFamily) return;
    const ref = this.dialog.open(EditFamilyDialogComponent, {
      width: '400px',
      data: { name: this.selectedFamily.name, description: this.selectedFamily.description ?? '' },
    });
    ref.afterClosed().subscribe((result) => {
      if (result) {
        this.familyService.updateFamily(this.selectedFamily!.id, result).subscribe({
          next: (updated) => {
            this.families = this.families.map((f) => (f.id === updated.id ? { ...f, ...updated } : f));
            this.selectedFamily = { ...this.selectedFamily!, ...updated };
            this.snackBar.open('Family updated', 'Close', { duration: 3000 });
          },
          error: () => this.snackBar.open('Failed to update family', 'Close', { duration: 5000 }),
        });
      }
    });
  }

  openAddMember(): void {
    if (!this.selectedFamily) return;
    const ref = this.dialog.open(AddMemberDialogComponent, { width: '400px' });
    ref.afterClosed().subscribe((result) => {
      if (result) {
        this.familyService.addMember(this.selectedFamily!.id, result).subscribe({
          next: (member) => {
            this.members = [...this.members, member];
            this.snackBar.open('Member added', 'Close', { duration: 3000 });
          },
          error: () => this.snackBar.open('Failed to add member', 'Close', { duration: 5000 }),
        });
      }
    });
  }
}
