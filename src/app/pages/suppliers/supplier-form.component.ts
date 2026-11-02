import { Component, computed, inject, input, OnInit, signal } from '@angular/core';
import {
  AbstractControl,
  FormBuilder,
  ReactiveFormsModule,
  ValidationErrors,
  Validators,
} from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { Supplier } from '../../core/mock-data';
import { SupplierStore } from '../../core/supplier-store';

/** Rejects values that are only whitespace. */
function notBlank(control: AbstractControl): ValidationErrors | null {
  return typeof control.value === 'string' && control.value.trim() === '' ? { blank: true } : null;
}

@Component({
  selector: 'oc-supplier-form',
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './supplier-form.component.html',
  styleUrls: ['../orders/orders.css', './suppliers.css'],
})
export class SupplierFormComponent implements OnInit {
  private readonly fb = inject(FormBuilder);
  private readonly router = inject(Router);
  private readonly store = inject(SupplierStore);

  readonly id = input<string>(); // present on /suppliers/:id/edit, absent on /suppliers/new
  readonly supplier = signal<Supplier | undefined>(undefined);
  readonly notFound = signal(false);
  readonly submitted = signal(false);
  readonly error = signal('');
  readonly cancelLink = computed(() => {
    const s = this.supplier();
    return s ? ['/suppliers', s.supplier_id] : ['/suppliers'];
  });

  readonly form = this.fb.nonNullable.group({
    supplier_name: ['', [Validators.required, notBlank]],
    contact_person: ['', [Validators.required, notBlank]],
    email: ['', [Validators.required, Validators.email]],
    phone: ['', [Validators.required, notBlank]],
    address: [''],
  });

  ngOnInit(): void {
    const raw = this.id();
    if (raw === undefined) return;

    const s = this.store.get(Number(raw));
    if (!s) {
      this.notFound.set(true);
      return;
    }
    this.supplier.set(s);
    this.form.patchValue({
      supplier_name: s.supplier_name,
      contact_person: s.contact_person,
      email: s.email,
      phone: s.phone,
      address: s.address,
    });
  }

  msg(c: AbstractControl): string {
    if (c.hasError('required') || c.hasError('blank')) return 'Required';
    if (c.hasError('email')) return 'Enter a valid email address';
    return '';
  }

  async save(): Promise<void> {
    this.submitted.set(true);
    this.error.set('');
    this.form.markAllAsTouched();
    if (this.form.invalid) return;

    const v = this.form.getRawValue();
    try {
      const existing = this.supplier();
      if (existing) {
        await this.store.update(existing.supplier_id, v);
        this.router.navigate(['/suppliers', existing.supplier_id]);
      } else {
        const newId = await this.store.create(v);
        this.router.navigate(['/suppliers', newId]);
      }
    } catch (e: any) {
      this.error.set(e instanceof Error ? e.message : 'Could not save the supplier.');
    }
  }
}