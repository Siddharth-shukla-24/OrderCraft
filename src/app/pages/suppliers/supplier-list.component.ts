import { Component, computed, inject, signal } from '@angular/core';
import { RouterLink } from '@angular/router';
import { RecordStatus, Supplier } from '../../core/mock-data';
import { SupplierStore } from '../../core/supplier-store';

@Component({
  selector: 'oc-supplier-list',
  imports: [RouterLink],
  templateUrl: './supplier-list.component.html',
  styleUrls: ['../orders/orders.css', './suppliers.css'],
})
export class SupplierListComponent {
  readonly store = inject(SupplierStore);

  readonly statuses: RecordStatus[] = ['ACTIVE', 'INACTIVE'];
  readonly search = signal('');
  readonly statusFilter = signal<'ALL' | RecordStatus>('ALL');

  readonly rows = computed(() => {
    const q = this.search().trim().toLowerCase();
    const status = this.statusFilter();

    return this.store
      .suppliers()
      .filter(s => status === 'ALL' || s.status === status)
      .filter(
        s =>
          !q ||
          [s.supplier_name, s.contact_person, s.email, s.phone, s.address].some(v => v.toLowerCase().includes(q)),
      )
      .sort((a, b) => a.supplier_name.localeCompare(b.supplier_name));
  });

  setStatus(value: string): void {
    this.statusFilter.set(value as 'ALL' | RecordStatus);
  }

  async toggle(s: Supplier): Promise<void> {
    if (s.status === 'ACTIVE') {
      if (!confirm(`Deactivate ${s.supplier_name}?`)) return;
      await this.store.setStatus(s.supplier_id, 'INACTIVE');
    } else {
      await this.store.setStatus(s.supplier_id, 'ACTIVE');
    }
  }
}