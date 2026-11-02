import { Component, inject } from '@angular/core';
import { DatePipe } from '@angular/common';
import { RouterLink } from '@angular/router';
import { AggregatePaymentStatus, inr, OrderStatus, ORDER_STATUSES } from '../../core/mock-data';
import { OrderStore } from '../../core/order-store';
import { SupplierStore } from '../../core/supplier-store';

interface OrderRow {
  id: number;
  customer: string;
  date: string;
  total: number;
  status: OrderStatus;
  paymentStatus: AggregatePaymentStatus | null;
  paymentClass: string;
}

const PAYMENT_CLASS: Record<AggregatePaymentStatus, string> = {
  PENDING: 'pending',
  'PARTIALLY PAID': 'partial',
  PAID: 'paid',
};

@Component({
  selector: 'oc-dashboard',
  imports: [DatePipe, RouterLink],
  templateUrl: './dashboard.component.html',
  styleUrl: './dashboard.component.css',
})
export class DashboardComponent {
  private readonly store = inject(OrderStore);
  private readonly suppliers = inject(SupplierStore);

  readonly inr = inr;

  private get rows(): OrderRow[] {
    return this.store.orders().map(o => {
      const bill = this.store.billOf(o.order_id);
      const paymentStatus = bill ? this.store.paymentStatusOf(bill) : null;
      return {
        id: o.order_id,
        customer: this.store.customerOf(o.customer_id)?.customer_name ?? 'Unknown',
        date: o.order_date,
        total: this.store.totalOf(o.order_id),
        status: o.status,
        paymentStatus,
        paymentClass: paymentStatus ? PAYMENT_CLASS[paymentStatus] : 'none',
      };
    });
  }

  get recent(): OrderRow[] {
    return [...this.rows].sort((a, b) => b.date.localeCompare(a.date) || b.id - a.id).slice(0, 5);
  }

  get stats() {
    const rows = this.rows;
    const outstandingAmount = this.store.bills().reduce((sum, b) => sum + this.store.amountDue(b), 0);
    return [
      {
        label: 'Total Orders',
        value: String(rows.length),
        icon: '📦',
        iconClass: 'kpi-icon-blue',
        barClass: 'bar-blue',
      },
      {
        label: 'Open Orders',
        value: String(rows.filter(r => r.status !== 'COMPLETED').length),
        icon: '⏳',
        iconClass: 'kpi-icon-amber',
        barClass: 'bar-amber',
      },
      {
        label: 'Outstanding Balance',
        value: inr(outstandingAmount),
        icon: '💳',
        iconClass: 'kpi-icon-red',
        barClass: 'bar-red',
      },
      {
        label: 'Active Suppliers',
        value: String(this.suppliers.activeCount()),
        icon: '🏭',
        iconClass: 'kpi-icon-emerald',
        barClass: 'bar-emerald',
      },
    ];
  }

  get breakdown() {
    const rows = this.rows;
    return ORDER_STATUSES.map(status => {
      const count = rows.filter(r => r.status === status).length;
      return { status, count, percent: rows.length ? Math.round((count / rows.length) * 100) : 0 };
    });
  }
}