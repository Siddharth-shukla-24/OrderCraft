import { Component, signal } from '@angular/core';
import { RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';

interface NavItem {
  path: string;
  label: string;
  icon: string;
}

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, RouterLink, RouterLinkActive],
  templateUrl: './app.html',
  styleUrl: './app.css',
})
export class App {
  readonly navOpen = signal(false); // mobile drawer
  readonly collapsed = signal(false); // desktop sidebar

  readonly nav: NavItem[] = [
    { path: '/dashboard', label: 'Dashboard', icon: '📊' },
    { path: '/orders', label: 'Order Processing', icon: '🛒' },
    { path: '/billing', label: 'Bill Generation', icon: '🧾' },
    { path: '/tracking', label: 'Order & Payment Tracking', icon: '💳' },
    { path: '/suppliers', label: 'Supplier Management', icon: '🏭' },
  ];

  toggleNav(): void {
    if (window.innerWidth <= 900) {
      this.navOpen.update(open => !open);
    } else {
      this.collapsed.update(c => !c);
    }
  }
}