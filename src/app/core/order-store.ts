import { inject, Injectable, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { firstValueFrom } from 'rxjs';
import {
  AggregatePaymentStatus,
  Bill,
  BILLS,
  Customer,
  CUSTOMERS,
  CUSTOMER_ORDERS,
  CustomerOrder,
  isValidPayment,
  ORDER_ITEMS,
  ORDER_STATUSES,
  OrderItem,
  OrderStatus,
  Payment,
  PAYMENTS,
  ROLES,
  USERS,
} from './mock-data';
import { isOrderEligibleForBill } from './bill-rules';
import { cents, paymentAmountError } from './payment-rules';
import { API_BASE_URL, extractErrorMessage } from './api.config';

export interface ItemDraft {
  order_item_id?: number;
  item_code: string;
  description: string;
  quantity: number;
  unit_price: number;
}

export interface NewOrderDraft {
  customer_id: number;
  required_date: string;
  items: ItemDraft[];
}

export interface OrderChanges {
  required_date: string;
  status: OrderStatus;
  items: ItemDraft[] | null; // null = leave line items untouched
}

export interface PaymentDraft {
  bill_id: number;
  payment_date: string;
  amount: number;
  method: string;
  reference_no: string;
}

export function todayISO(): string {
  const d = new Date();
  const mm = String(d.getMonth() + 1).padStart(2, '0');
  const dd = String(d.getDate()).padStart(2, '0');
  return `${d.getFullYear()}-${mm}-${dd}`;
}

@Injectable({ providedIn: 'root' })
export class OrderStore {
  private readonly http = inject(HttpClient);

  readonly orders = signal<CustomerOrder[]>([...CUSTOMER_ORDERS]);
  readonly items = signal<OrderItem[]>([...ORDER_ITEMS]);
  readonly bills = signal<Bill[]>([...BILLS]);
  readonly payments = signal<Payment[]>([...PAYMENTS]);
  readonly customers = signal<Customer[]>([...CUSTOMERS]);

  private readonly operatorId =
    USERS.find(u => ROLES.find(r => r.role_id === u.role_id)?.role_name === 'Order operator')?.user_id ??
    USERS[0].user_id;

  constructor() {
    this.refreshAll();
  }

  async refreshAll(): Promise<void> {
    await Promise.allSettled([
      this.refreshCustomers(),
      this.refreshOrders(),
      this.refreshBills(),
      this.refreshPayments(),
    ]);
  }

  async refreshCustomers(): Promise<void> {
    try {
      const list = await firstValueFrom(this.http.get<Customer[]>(`${API_BASE_URL}/customers`));
      if (list && list.length > 0) {
        this.customers.set(list);
      }
    } catch {
      // Backend not reached; preserve existing signal
    }
  }

  async refreshOrders(): Promise<void> {
    try {
      const list = await firstValueFrom(this.http.get<any[]>(`${API_BASE_URL}/orders`));
      if (!list) return;
      const newOrders: CustomerOrder[] = [];
      const newItems: OrderItem[] = [];

      list.forEach(o => {
        newOrders.push({
          order_id: o.order_id,
          customer_id: o.customer_id,
          created_by: o.created_by ?? this.operatorId,
          order_date: o.order_date,
          required_date: o.required_date,
          status: o.status,
          total_amount: Number(o.total_amount),
        });

        if (o.items && Array.isArray(o.items)) {
          o.items.forEach((item: any) => {
            newItems.push({
              order_item_id: item.order_item_id,
              order_id: item.order_id ?? o.order_id,
              item_code: item.item_code,
              description: item.description,
              quantity: Number(item.quantity),
              unit_price: Number(item.unit_price),
              line_total: Number(item.line_total),
            });
          });
        }
      });

      this.orders.set(newOrders);
      this.items.set(newItems);
    } catch {
      // Backend not reached; preserve existing signal
    }
  }

  async refreshBills(): Promise<void> {
    try {
      const list = await firstValueFrom(this.http.get<Bill[]>(`${API_BASE_URL}/bills`));
      if (list) {
        this.bills.set(list);
      }
    } catch {
      // Backend not reached; preserve existing signal
    }
  }

  async refreshPayments(): Promise<void> {
    try {
      const list = await firstValueFrom(this.http.get<Payment[]>(`${API_BASE_URL}/payments`));
      if (list) {
        this.payments.set(list);
      }
    } catch {
      // Backend not reached; preserve existing signal
    }
  }

  loadPaymentsForBill(bill_id: number): void {
    this.http.get<Payment[]>(`${API_BASE_URL}/bills/${bill_id}/payments`).subscribe({
      next: (list) => {
        if (!list) return;
        this.payments.update(existing => [
          ...existing.filter(p => p.bill_id !== bill_id),
          ...list,
        ]);
      },
      error: () => {},
    });
  }

  // ------------------------------------------------------------ orders
  get(order_id: number): CustomerOrder | undefined {
    return this.orders().find(o => o.order_id === order_id);
  }

  itemsOf(order_id: number): OrderItem[] {
    return this.items().filter(i => i.order_id === order_id);
  }

  totalOf(order_id: number): number {
    return this.itemsOf(order_id).reduce((sum, i) => sum + i.line_total, 0);
  }

  customerOf(customer_id: number): Customer | undefined {
    return this.customers().find(c => c.customer_id === customer_id);
  }

  userName(user_id: number): string {
    return USERS.find(u => u.user_id === user_id)?.full_name ?? '—';
  }

  allowedStatuses(current: OrderStatus): OrderStatus[] {
    const i = ORDER_STATUSES.indexOf(current);
    return ORDER_STATUSES.slice(i, i + 2);
  }

  async create(draft: NewOrderDraft): Promise<number> {
    const payload = {
      customer_id: draft.customer_id,
      required_date: draft.required_date,
      items: draft.items.map(d => ({
        item_code: d.item_code.trim(),
        description: d.description.trim(),
        quantity: d.quantity,
        unit_price: d.unit_price,
      })),
    };

    try {
      const res = await firstValueFrom(this.http.post<any>(`${API_BASE_URL}/orders`, payload));
      await this.refreshOrders();
      return res.order_id;
    } catch (e: any) {
      throw new Error(extractErrorMessage(e));
    }
  }

  async update(order_id: number, changes: OrderChanges): Promise<void> {
    const payload = {
      required_date: changes.required_date,
      status: changes.status,
      items: changes.items ? changes.items.map(d => ({
        order_item_id: d.order_item_id || undefined,
        item_code: d.item_code.trim(),
        description: d.description.trim(),
        quantity: d.quantity,
        unit_price: d.unit_price,
      })) : null,
    };

    try {
      await firstValueFrom(this.http.put<any>(`${API_BASE_URL}/orders/${order_id}`, payload));
      await this.refreshOrders();
    } catch (e: any) {
      throw new Error(extractErrorMessage(e));
    }
  }

  // ------------------------------------------------------------ bills
  billOf(order_id: number): Bill | undefined {
    return this.bills().find(b => b.order_id === order_id);
  }

  billById(bill_id: number): Bill | undefined {
    return this.bills().find(b => b.bill_id === bill_id);
  }

  billBlocker(order_id: number): string | null {
    const order = this.get(order_id);
    if (!order) return 'Order not found.';
    if (this.billOf(order_id)) return 'This order already has a bill.';
    if (this.itemsOf(order_id).length === 0) return 'The order has no line items.';
    if (!isOrderEligibleForBill(order)) return 'This order is not eligible for billing.';
    return null;
  }

  async generateBill(order_id: number): Promise<Bill> {
    const reason = this.billBlocker(order_id);
    if (reason) throw new Error(reason);

    try {
      const bill = await firstValueFrom(this.http.post<Bill>(`${API_BASE_URL}/orders/${order_id}/bill`, {}));
      this.bills.update(list => [...list, bill]);
      await this.refreshOrders();
      return bill;
    } catch (e: any) {
      throw new Error(extractErrorMessage(e));
    }
  }

  async issueBill(bill_id: number): Promise<void> {
    try {
      const updated = await firstValueFrom(this.http.post<Bill>(`${API_BASE_URL}/bills/${bill_id}/issue`, {}));
      this.bills.update(list =>
        list.map((b): Bill => (b.bill_id === bill_id ? updated : b)),
      );
    } catch (e: any) {
      throw new Error(extractErrorMessage(e));
    }
  }

  // ------------------------------------------------------------ payments
  paymentsOf(bill_id: number): Payment[] {
    return this.payments().filter(p => p.bill_id === bill_id);
  }

  paidAmount(bill_id: number): number {
    return this.paymentsOf(bill_id)
      .filter(isValidPayment)
      .reduce((sum, p) => sum + p.amount, 0);
  }

  amountDue(bill: Bill): number {
    return bill.total_amount - this.paidAmount(bill.bill_id);
  }

  paymentStatusOf(bill: Bill): AggregatePaymentStatus {
    const paid = cents(this.paidAmount(bill.bill_id));
    if (paid <= 0) return 'PENDING';
    return paid >= cents(bill.total_amount) ? 'PAID' : 'PARTIALLY PAID';
  }

  async recordPayment(draft: PaymentDraft): Promise<Payment> {
    const bill = this.billById(draft.bill_id);
    if (!bill) throw new Error('A payment can only be recorded against an existing bill.');
    if (!draft.payment_date) throw new Error('Payment date is required.');
    if (!draft.method.trim()) throw new Error('Payment method is required.');
    const amountError = paymentAmountError(draft.amount, this.amountDue(bill));
    if (amountError) throw new Error(amountError);

    const payload = {
      payment_date: draft.payment_date,
      amount: draft.amount,
      method: draft.method.trim(),
      reference_no: draft.reference_no.trim(),
    };

    try {
      const payment = await firstValueFrom(
        this.http.post<Payment>(`${API_BASE_URL}/bills/${draft.bill_id}/payments`, payload)
      );
      this.payments.update(list => [...list, payment]);
      await this.refreshBills();
      return payment;
    } catch (e: any) {
      throw new Error(extractErrorMessage(e));
    }
  }
}