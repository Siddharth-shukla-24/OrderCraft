import { Component, computed, inject, input, signal } from '@angular/core';
import { DatePipe } from '@angular/common';
import {
  AbstractControl,
  FormBuilder,
  FormControl,
  ReactiveFormsModule,
  ValidationErrors,
  Validators,
} from '@angular/forms';
import { RouterLink } from '@angular/router';
import { inr } from '../../core/mock-data';
import { OrderStore, todayISO } from '../../core/order-store';
import { paymentAmountError, paymentClass } from '../../core/payment-rules';

@Component({
  selector: 'oc-tracking-detail',
  imports: [ReactiveFormsModule, RouterLink, DatePipe],
  templateUrl: './tracking-detail.component.html',
  styleUrls: ['../orders/orders.css', '../bills/bills.css', './tracking.css'],
})
export class TrackingDetailComponent {
  readonly store = inject(OrderStore);
  private readonly fb = inject(FormBuilder);

  readonly orderId = input<string>(); // bound from the :orderId route parameter
  readonly inr = inr;
  readonly methodSuggestions = ['Bank transfer', 'Cheque', 'Cash'];

  readonly order = computed(() => this.store.get(Number(this.orderId())));
  readonly customer = computed(() => {
    const o = this.order();
    return o ? this.store.customerOf(o.customer_id) : undefined;
  });
  readonly bill = computed(() => {
    const o = this.order();
    return o ? this.store.billOf(o.order_id) : undefined;
  });
  readonly payments = computed(() => {
    const b = this.bill();
    if (!b) return [];
    return [...this.store.paymentsOf(b.bill_id)].sort(
      (x, y) => y.payment_date.localeCompare(x.payment_date) || y.payment_id - x.payment_id,
    );
  });
  readonly paid = computed(() => {
    const b = this.bill();
    return b ? this.store.paidAmount(b.bill_id) : 0;
  });
  readonly due = computed(() => {
    const b = this.bill();
    return b ? this.store.amountDue(b) : 0;
  });
  readonly status = computed(() => {
    const b = this.bill();
    return b ? this.store.paymentStatusOf(b) : null;
  });
  readonly statusClass = computed(() => paymentClass(this.status()));

  readonly submitted = signal(false);
  readonly error = signal('');

  private readonly amountValidator = (c: AbstractControl): ValidationErrors | null => {
    if (c.value === null || c.value === '') return null; // "required" reports this case
    const message = paymentAmountError(Number(c.value), this.due());
    return message ? { amount: message } : null;
  };

  readonly form = this.fb.nonNullable.group({
    payment_date: [todayISO(), Validators.required],
    amount: new FormControl<number | null>(null, [Validators.required, this.amountValidator]),
    method: ['', Validators.required],
    reference_no: [''],
  });

  msg(c: AbstractControl): string {
    if (c.hasError('required')) return 'Required';
    const amount = c.getError('amount');
    return typeof amount === 'string' ? amount : '';
  }

  payInFull(): void {
    this.form.controls.amount.setValue(this.due());
    this.form.controls.amount.markAsTouched();
  }

  async save(): Promise<void> {
    this.submitted.set(true);
    this.error.set('');
    this.form.markAllAsTouched();

    const b = this.bill();
    if (!b || this.form.invalid) return;

    const v = this.form.getRawValue();
    try {
      await this.store.recordPayment({
        bill_id: b.bill_id,
        payment_date: v.payment_date,
        amount: Number(v.amount),
        method: v.method,
        reference_no: v.reference_no,
      });
      this.form.reset({ payment_date: todayISO(), amount: null, method: '', reference_no: '' });
      this.submitted.set(false);
    } catch (e) {
      this.error.set(e instanceof Error ? e.message : 'Could not record the payment.');
    }
  }
}