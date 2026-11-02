import { Component, computed, inject, input } from '@angular/core';
import { DatePipe } from '@angular/common';
import { RouterLink } from '@angular/router';
import { inr } from '../../core/mock-data';
import { OrderStore } from '../../core/order-store';

@Component({
  selector: 'oc-bill-detail',
  imports: [RouterLink, DatePipe],
  templateUrl: './bill-detail.component.html',
  styleUrls: ['../orders/orders.css', './bills.css'],
})
export class BillDetailComponent {
  private readonly store = inject(OrderStore);

  readonly id = input<string>(); // bound from the :id route parameter
  readonly inr = inr;

  readonly bill = computed(() => this.store.billById(Number(this.id())));
  readonly order = computed(() => {
    const b = this.bill();
    return b ? this.store.get(b.order_id) : undefined;
  });
  readonly customer = computed(() => {
    const o = this.order();
    return o ? this.store.customerOf(o.customer_id) : undefined;
  });
  readonly items = computed(() => {
    const b = this.bill();
    return b ? this.store.itemsOf(b.order_id) : [];
  });

  async issue(): Promise<void> {
    const b = this.bill();
    if (b && confirm(`Issue bill ${b.bill_number}? This cannot be undone.`)) {
      await this.store.issueBill(b.bill_id);
    }
  }
}