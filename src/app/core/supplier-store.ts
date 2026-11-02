import { computed, inject, Injectable, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { firstValueFrom } from 'rxjs';
import { RecordStatus, Supplier, SUPPLIERS } from './mock-data';
import { API_BASE_URL, extractErrorMessage } from './api.config';

export interface SupplierDraft {
  supplier_name: string;
  contact_person: string;
  email: string;
  phone: string;
  address: string;
}

function clean(d: SupplierDraft): SupplierDraft {
  return {
    supplier_name: d.supplier_name.trim(),
    contact_person: d.contact_person.trim(),
    email: d.email.trim(),
    phone: d.phone.trim(),
    address: d.address.trim(),
  };
}

@Injectable({ providedIn: 'root' })
export class SupplierStore {
  private readonly http = inject(HttpClient);

  readonly suppliers = signal<Supplier[]>([...SUPPLIERS]);
  readonly activeCount = computed(() => this.suppliers().filter(s => s.status === 'ACTIVE').length);

  constructor() {
    this.refresh();
  }

  async refresh(): Promise<void> {
    try {
      const list = await firstValueFrom(this.http.get<Supplier[]>(`${API_BASE_URL}/suppliers`));
      if (list && list.length > 0) {
        this.suppliers.set(list);
      }
    } catch {
      // Backend not reached; keep existing signal
    }
  }

  get(supplier_id: number): Supplier | undefined {
    return this.suppliers().find(s => s.supplier_id === supplier_id);
  }

  async create(draft: SupplierDraft): Promise<number> {
    const payload = clean(draft);
    try {
      const res = await firstValueFrom(this.http.post<Supplier>(`${API_BASE_URL}/suppliers`, payload));
      this.suppliers.update(list => [...list, res]);
      return res.supplier_id;
    } catch (e: any) {
      throw new Error(extractErrorMessage(e));
    }
  }

  async update(supplier_id: number, draft: SupplierDraft): Promise<void> {
    const payload = clean(draft);
    try {
      const res = await firstValueFrom(this.http.put<Supplier>(`${API_BASE_URL}/suppliers/${supplier_id}`, payload));
      this.suppliers.update(list => list.map(s => (s.supplier_id === supplier_id ? res : s)));
    } catch (e: any) {
      throw new Error(extractErrorMessage(e));
    }
  }

  async setStatus(supplier_id: number, status: RecordStatus): Promise<void> {
    try {
      const res = await firstValueFrom(this.http.patch<Supplier>(`${API_BASE_URL}/suppliers/${supplier_id}/status`, { status }));
      this.suppliers.update(list => list.map(s => (s.supplier_id === supplier_id ? res : s)));
    } catch (e: any) {
      throw new Error(extractErrorMessage(e));
    }
  }
}