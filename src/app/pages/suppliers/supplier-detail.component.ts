import { Component, computed, inject, input } from '@angular/core';
import { RouterLink } from '@angular/router';
import { SupplierStore } from '../../core/supplier-store';

@Component({
  selector: 'oc-supplier-detail',
  imports: [RouterLink],
  templateUrl: './supplier-detail.component.html',
  styleUrls: ['../orders/orders.css', './suppliers.css'],
})
export class SupplierDetailComponent {
  private readonly store = inject(SupplierStore);

  readonly id = input<string>(); // bound from the :id route parameter
  readonly supplier = computed(() => this.store.get(Number(this.id())));

  async toggle(): Promise<void> {
    const s = this.supplier();
    if (!s) return;
    if (s.status === 'ACTIVE') {
      if (!confirm(`Deactivate ${s.supplier_name}?`)) return;
      await this.store.setStatus(s.supplier_id, 'INACTIVE');
    } else {
      await this.store.setStatus(s.supplier_id, 'ACTIVE');
    }
  }
}