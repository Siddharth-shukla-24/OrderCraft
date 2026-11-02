import { Component, computed, inject, signal } from '@angular/core';
import { RouterLink } from '@angular/router';
import { AggregatePaymentStatus, inr, ORDER_STATUSES, OrderStatus } from '../../core/mock-data';
import { OrderStore } from '../../core/order-store';
import { paymentClass } from '../../core/payment-rules';

type PaymentFilter = 'ALL' | AggregatePaymentStatus | 'NO BILL';

@Component({
  selector: 'oc-tracking-list',
  imports: [RouterLink],
  templateUrl: './tracking-list.component.html',
  styleUrls: ['../orders/orders.css', '../bills/bills.css', './tracking.css'],
})
export class TrackingListComponent {
  private readonly store = inject(OrderStore);

  readonly inr = inr;
  readonly orderStatuses = ORDER_STATUSES;
  readonly paymentStatuses: AggregatePaymentStatus[] = ['PENDING', 'PARTIALLY PAID', 'PAID'];

  readonly search = signal('');
  readonly orderFilter = signal<'ALL' | OrderStatus>('ALL');
  readonly paymentFilter = signal<PaymentFilter>('ALL');

  readonly rows = computed(() => {
    const q = this.search().trim().toLowerCase().replace(/^#/, '');
    const orderStatus = this.orderFilter();
    const payment = this.paymentFilter();

    return this.store
      .orders()
      .map(o => {
        const bill = this.store.billOf(o.order_id);
        const status = bill ? this.store.paymentStatusOf(bill) : null;
        return {
          id: o.order_id,
          customer: this.store.customerOf(o.customer_id)?.customer_name ?? 'Unknown',
          orderStatus: o.status,
          billNumber: bill?.bill_number ?? null,
          billStatus: bill?.status ?? null,
          billTotal: bill?.total_amount ?? 0,
          paid: bill ? this.store.paidAmount(bill.bill_id) : 0,
          due: bill ? this.store.amountDue(bill) : 0,
          paymentStatus: status,
          paymentClass: paymentClass(status),
        };
      })
      .filter(r => orderStatus === 'ALL' || r.orderStatus === orderStatus)
      .filter(
        r =>
          payment === 'ALL' ||
          (payment === 'NO BILL' ? r.paymentStatus === null : r.paymentStatus === payment),
      )
      .filter(
        r =>
          !q ||
          String(r.id).includes(q) ||
          r.customer.toLowerCase().includes(q) ||
          (r.billNumber?.toLowerCase().includes(q) ?? false),
      )
      .sort((a, b) => b.id - a.id);
  });

  setOrderStatus(value: string): void {
    this.orderFilter.set(value as 'ALL' | OrderStatus);
  }

  setPaymentFilter(value: string): void {
    this.paymentFilter.set(value as PaymentFilter);
  }
}