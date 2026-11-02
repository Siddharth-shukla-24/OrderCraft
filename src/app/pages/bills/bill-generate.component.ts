import { Component, computed, inject, signal } from '@angular/core';
import { Router, RouterLink } from '@angular/router';
import { BILL_ELIGIBILITY_NOTE, PROTOTYPE_TAX_AMOUNT } from '../../core/bill-rules';
import { inr } from '../../core/mock-data';
import { OrderStore } from '../../core/order-store';

@Component({
  selector: 'oc-bill-generate',
  imports: [RouterLink],
  templateUrl: './bill-generate.component.html',
  styleUrls: ['../orders/orders.css', './bills.css'],
})
export class BillGenerateComponent {
  private readonly store = inject(OrderStore);
  private readonly router = inject(Router);

  readonly inr = inr;
  readonly eligibilityNote = BILL_ELIGIBILITY_NOTE;
  readonly tax = PROTOTYPE_TAX_AMOUNT;

  readonly selectedId = signal(0);
  readonly error = signal('');

  /** Orders that pass the one-bill-per-order check and the (pending) eligibility rule. */
  readonly candidates = computed(() =>
    this.store
      .orders()
      .filter(o => this.store.billBlocker(o.order_id) === null)
      .map(o => ({
        id: o.order_id,
        label: `#${o.order_id} — ${this.store.customerOf(o.customer_id)?.customer_name ?? 'Unknown'} — ${inr(
          this.store.totalOf(o.order_id),
        )} — ${o.status}`,
      }))
      .sort((a, b) => b.id - a.id),
  );

  readonly order = computed(() => this.store.get(this.selectedId()));
  readonly customer = computed(() => {
    const o = this.order();
    return o ? this.store.customerOf(o.customer_id) : undefined;
  });
  readonly items = computed(() => this.store.itemsOf(this.selectedId()));
  readonly subtotal = computed(() => this.store.totalOf(this.selectedId()));
  readonly total = computed(() => this.subtotal() + this.tax);

  select(value: string): void {
    this.selectedId.set(Number(value));
    this.error.set('');
  }

  async generate(): Promise<void> {
    try {
      const bill = await this.store.generateBill(this.selectedId());
      this.router.navigate(['/billing', bill.bill_id]);
    } catch (e) {
      this.error.set(e instanceof Error ? e.message : 'Could not generate the bill.');
    }
  }
}