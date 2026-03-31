import { Component, inject } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { AuthService } from '../../../core/services/auth.service';

@Component({
  selector: 'app-signup',
  standalone: true,
  imports: [MatCardModule, MatButtonModule],
  template: `
    <div class="signup-container">
      <mat-card>
        <mat-card-header>
          <mat-card-title>Create Account</mat-card-title>
        </mat-card-header>
        <mat-card-content>
          <p>Create a new account to start managing your family budget.</p>
        </mat-card-content>
        <mat-card-actions>
          <button mat-raised-button color="primary" (click)="register()">
            Create Account
          </button>
        </mat-card-actions>
      </mat-card>
    </div>
  `,
  styles: [`
    .signup-container {
      display: flex;
      justify-content: center;
      align-items: center;
      min-height: 100vh;
      padding: 16px;
    }
    mat-card {
      max-width: 400px;
      width: 100%;
      text-align: center;
    }
    mat-card-actions {
      justify-content: center;
      padding-bottom: 16px;
    }
  `],
})
export class SignupComponent {
  private readonly auth = inject(AuthService);
  private readonly route = inject(ActivatedRoute);

  async register(): Promise<void> {
    const returnUrl = this.route.snapshot.queryParams['returnUrl'] || '/settings';
    await this.auth.register(window.location.origin + returnUrl);
  }
}
