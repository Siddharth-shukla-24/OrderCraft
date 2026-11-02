import { Component, computed, inject, signal } from '@angular/core';
import { DatePipe } from '@angular/common';
import { RouterLink } from '@angular/router';
import { BillStatus, inr } from '../../core/mock-data';
import { OrderStore } from '../../core/order-store';

@Component({
  selector: 'oc-bill-list',
  imports: [RouterLink, DatePipe],
  templateUrl: './bill-list.component.html',
  styleUrls: ['../orders/orders.css', './bills.css'],
})
export class BillListComponent {
  private readonly store = inject(OrderStore);

  readonly inr = inr;
  readonly statuses: BillStatus[] = ['GENERATED', 'ISSUED'];
  readonly search = signal('');
  readonly statusFilter = signal<'ALL' | BillStatus>('ALL');

  readonly readyCount = computed(
    () => this.store.orders().filter(o => this.store.billBlocker(o.order_id) === null).length,
  );

  readonly rows = computed(() => {
    const q = this.search().trim().toLowerCase().replace(/^#/, '');
    const status = this.statusFilter();

    return this.store
      .bills()
      .map(b => {
        const order = this.store.get(b.order_id);
        return {
          id: b.bill_id,
          number: b.bill_number,
          orderId: b.order_id,
          customer: order ? (this.store.customerOf(order.customer_id)?.customer_name ?? 'Unknown') : 'Unknown',
          date: b.bill_date,
          subtotal: b.subtotal,
          tax: b.tax_amount,
          total: b.total_amount,
          status: b.status,
        };
      })
      .filter(r => status === 'ALL' || r.status === status)
      .filter(
        r =>
          !q ||
          r.number.toLowerCase().includes(q) ||
          String(r.orderId).includes(q) ||
          r.customer.toLowerCase().includes(q),
      )
      .sort((a, b) => b.date.localeCompare(a.date) || b.id - a.id);
  });

  setStatus(value: string): void {
    this.statusFilter.set(value as 'ALL' | BillStatus);
  }
}