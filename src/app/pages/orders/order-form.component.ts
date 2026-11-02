import { Component, computed, inject, input, OnInit, signal } from '@angular/core';
import {
  AbstractControl,
  FormBuilder,
  ReactiveFormsModule,
  ValidationErrors,
  Validators,
} from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { CUSTOMERS, CustomerOrder, inr, OrderStatus } from '../../core/mock-data';
import { ItemDraft, OrderStore } from '../../core/order-store';

/** Quantity must be greater than 0. */
function positive(control: AbstractControl): ValidationErrors | null {
  return Number(control.value) > 0 ? null : { positive: true };
}

function itemGroup(fb: FormBuilder, v?: ItemDraft) {
  return fb.nonNullable.group({
    order_item_id: [v?.order_item_id ?? 0],
    item_code: [v?.item_code ?? '', Validators.required],
    description: [v?.description ?? '', Validators.required],
    quantity: [v?.quantity ?? 1, [Validators.required, positive]],
    unit_price: [v?.unit_price ?? 0, [Validators.required, Validators.min(0)]],
  });
}
type ItemGroup = ReturnType<typeof itemGroup>;

@Component({
  selector: 'oc-order-form',
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './order-form.component.html',
  styleUrl: './orders.css',
})
export class OrderFormComponent implements OnInit {
  private readonly fb = inject(FormBuilder);
  private readonly router = inject(Router);
  readonly store = inject(OrderStore);

  readonly id = input<string>(); // present on /orders/:id/edit, absent on /orders/new
  readonly inr = inr;

  get customers() {
    return this.store.customers().filter(c => c.status === 'ACTIVE');
  }

  readonly order = signal<CustomerOrder | undefined>(undefined);
  readonly notFound = signal(false);
  readonly locked = signal(false); // line items locked once a bill exists
  readonly statuses = signal<OrderStatus[]>([]);
  readonly submitted = signal(false);
  readonly error = signal('');
  readonly cancelLink = computed(() => {
    const o = this.order();
    return o ? ['/orders', o.order_id] : ['/orders'];
  });

  readonly items = this.fb.nonNullable.array<ItemGroup>([]);
  readonly form = this.fb.nonNullable.group({
    customer_id: [0, Validators.min(1)],
    required_date: ['', Validators.required],
    status: ['NEW' as OrderStatus],
    items: this.items,
  });

  ngOnInit(): void {
    const raw = this.id();
    if (raw === undefined) {
      this.items.push(itemGroup(this.fb));
      return;
    }

    const o = this.store.get(Number(raw));
    if (!o) {
      this.notFound.set(true);
      return;
    }

    this.order.set(o);
    this.form.patchValue({ customer_id: o.customer_id, required_date: o.required_date, status: o.status });
    this.store.itemsOf(o.order_id).forEach(i => this.items.push(itemGroup(this.fb, i)));
    this.statuses.set(this.store.allowedStatuses(o.status));

    if (this.store.billOf(o.order_id)) {
      this.locked.set(true);
      this.items.disable();
    }
  }

  addItem(): void {
    this.items.push(itemGroup(this.fb));
  }

  removeItem(index: number): void {
    if (this.items.length > 1) this.items.removeAt(index);
  }

  lineTotal(g: ItemGroup): number {
    return (Number(g.controls.quantity.value) || 0) * (Number(g.controls.unit_price.value) || 0);
  }

  total(): number {
    return this.items.controls.reduce((sum, g) => sum + this.lineTotal(g), 0);
  }

  msg(c: AbstractControl): string {
    if (c.hasError('required')) return 'Required';
    if (c.hasError('positive')) return 'Must be greater than 0';
    if (c.hasError('min')) return 'Cannot be negative';
    return '';
  }

  async save(): Promise<void> {
    this.submitted.set(true);
    this.error.set('');
    this.form.markAllAsTouched();
    if (this.form.invalid) return;

    const v = this.form.getRawValue();
    const drafts: ItemDraft[] = v.items.map(i => ({
      order_item_id: i.order_item_id || undefined,
      item_code: i.item_code,
      description: i.description,
      quantity: Number(i.quantity),
      unit_price: Number(i.unit_price),
    }));

    try {
      const existing = this.order();
      if (existing) {
        await this.store.update(existing.order_id, {
          required_date: v.required_date,
          status: v.status,
          items: this.locked() ? null : drafts,
        });
        this.router.navigate(['/orders', existing.order_id]);
      } else {
        const newId = await this.store.create({
          customer_id: v.customer_id,
          required_date: v.required_date,
          items: drafts,
        });
        this.router.navigate(['/orders', newId]);
      }
    } catch (e: any) {
      this.error.set(e instanceof Error ? e.message : 'Could not save the order.');
    }
  }
}