import { Component, computed, inject, signal } from '@angular/core';
import { DatePipe } from '@angular/common';
import { RouterLink } from '@angular/router';
import { inr, ORDER_STATUSES, OrderStatus } from '../../core/mock-data';
import { OrderStore } from '../../core/order-store';

@Component({
  selector: 'oc-order-list',
  imports: [RouterLink, DatePipe],
  templateUrl: './order-list.component.html',
  styleUrl: './orders.css',
})
export class OrderListComponent {
  private readonly store = inject(OrderStore);

  readonly inr = inr;
  readonly statuses = ORDER_STATUSES;
  readonly search = signal('');
  readonly statusFilter = signal<'ALL' | OrderStatus>('ALL');

  readonly rows = computed(() => {
    const q = this.search().trim().toLowerCase().replace(/^#/, '');
    const status = this.statusFilter();

    return this.store
      .orders()
      .map(o => ({
        id: o.order_id,
        customer: this.store.customerOf(o.customer_id)?.customer_name ?? 'Unknown',
        orderDate: o.order_date,
        requiredDate: o.required_date,
        itemCount: this.store.itemsOf(o.order_id).length,
        total: this.store.totalOf(o.order_id),
        status: o.status,
        billNumber: this.store.billOf(o.order_id)?.bill_number ?? null,
      }))
      .filter(r => status === 'ALL' || r.status === status)
      .filter(
        r =>
          !q ||
          String(r.id).includes(q) ||
          r.customer.toLowerCase().includes(q) ||
          (r.billNumber?.toLowerCase().includes(q) ?? false),
      )
      .sort((a, b) => b.orderDate.localeCompare(a.orderDate) || b.id - a.id);
  });

  setStatus(value: string): void {
    this.statusFilter.set(value as 'ALL' | OrderStatus);
  }
}