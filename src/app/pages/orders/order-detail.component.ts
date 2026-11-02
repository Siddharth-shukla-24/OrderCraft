import { Component, computed, inject, input } from '@angular/core';
import { DatePipe } from '@angular/common';
import { RouterLink } from '@angular/router';
import { inr } from '../../core/mock-data';
import { OrderStore } from '../../core/order-store';

@Component({
  selector: 'oc-order-detail',
  imports: [RouterLink, DatePipe],
  templateUrl: './order-detail.component.html',
  styleUrl: './orders.css',
})
export class OrderDetailComponent {
  readonly store = inject(OrderStore);
  readonly id = input<string>(); // bound from the :id route parameter
  readonly inr = inr;

  readonly order = computed(() => this.store.get(Number(this.id())));
  readonly items = computed(() => {
    const o = this.order();
    return o ? this.store.itemsOf(o.order_id) : [];
  });
  readonly total = computed(() => this.items().reduce((sum, i) => sum + i.line_total, 0));
  readonly customer = computed(() => {
    const o = this.order();
    return o ? this.store.customerOf(o.customer_id) : undefined;
  });
  readonly bill = computed(() => {
    const o = this.order();
    return o ? this.store.billOf(o.order_id) : undefined;
  });
}