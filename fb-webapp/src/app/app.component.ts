import { Component, inject, OnInit } from '@angular/core';
import { RouterOutlet, RouterLink } from '@angular/router';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatSidenavModule, MatSidenav } from '@angular/material/sidenav';
import { MatListModule } from '@angular/material/list';
import { MatTooltipModule } from '@angular/material/tooltip';
import { BreakpointObserver, Breakpoints } from '@angular/cdk/layout';

import { AuthService } from './core/services/auth.service';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [
    RouterOutlet,
    RouterLink,
    MatToolbarModule,
    MatButtonModule,
    MatIconModule,
    MatSidenavModule,
    MatListModule,
    MatTooltipModule
  ],
  templateUrl: './app.component.html',
  styleUrl: './app.component.scss',
})
export class AppComponent implements OnInit {

  private readonly auth = inject(AuthService);
  private readonly breakpointObserver = inject(BreakpointObserver);

  isMobile = false;
  isCollapsed = false;

  ngOnInit(): void {
    this.breakpointObserver.observe([Breakpoints.Handset])
      .subscribe(result => {
        this.isMobile = result.matches;

        if (this.isMobile) {
          this.isCollapsed = false; // overlay mode ignores collapse
        } else {
          // restore saved state (optional)
          const saved = localStorage.getItem('sidebarCollapsed');
          this.isCollapsed = saved === 'true';
        }
      });
  }

  toggleSidebar(sidenav: MatSidenav): void {
    if (this.isMobile) {
      sidenav.toggle(); // overlay open/close
    } else {
      this.isCollapsed = !this.isCollapsed;
      localStorage.setItem('sidebarCollapsed', String(this.isCollapsed));
    }
  }

  closeIfMobile(sidenav: MatSidenav): void {
    if (this.isMobile) {
      sidenav.close();
    }
  }

  get isAuthenticated(): boolean {
    return this.auth.isAuthenticated();
  }

  get username(): string | null {
    return this.auth.getCurrentUser()?.username ?? null;
  }

  async logout(): Promise<void> {
    await this.auth.logout();
  }
}
