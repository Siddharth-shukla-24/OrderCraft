import { Component, input } from '@angular/core';

@Component({
  selector: 'oc-placeholder',
  template: `
    <section class="card">
      <span class="tag">Coming in a later milestone</span>
      <h2>{{ heading() }}</h2>
      <p>{{ summary() }}</p>
      <h3>Planned scope</h3>
      <ul>
        @for (item of scope(); track item) {
          <li>{{ item }}</li>
        }
      </ul>
    </section>
  `,
  styles: `
    .card { background: var(--surface); border: 1px solid var(--border); border-radius: var(--radius); padding: 28px; max-width: 640px; }
    .tag { display: inline-block; background: #eff6ff; color: var(--primary); font-size: 12px; font-weight: 600; padding: 4px 10px; border-radius: 999px; margin-bottom: 14px; }
    p { color: var(--muted); margin: 8px 0 22px; }
    h3 { font-size: 14px; margin-bottom: 8px; }
    ul { margin: 0; padding-left: 20px; color: var(--muted); line-height: 1.8; }
  `,
})
export class PlaceholderComponent {
  readonly heading = input('');
  readonly summary = input('');
  readonly scope = input<string[]>([]);
}