// Mock data mirroring the OrderCraft DRD relational schema (section 5).
// Property names are the DRD column names verbatim. Frontend-only; no backend.

export type RecordStatus = 'ACTIVE' | 'INACTIVE';
export type OrderStatus = 'NEW' | 'CONFIRMED' | 'PROCESSING' | 'COMPLETED';
export type BillStatus = 'GENERATED' | 'ISSUED';
export type AggregatePaymentStatus = 'PENDING' | 'PARTIALLY PAID' | 'PAID';

export interface Role {
  role_id: number;
  role_name: string;
  description: string;
}

export interface User {
  user_id: number;
  role_id: number; // FK -> ROLES
  full_name: string;
  email: string;
  password_hash: string;
  status: RecordStatus;
}

export interface Customer {
  customer_id: number;
  customer_name: string;
  email: string;
  phone: string;
  billing_address: string;
  status: RecordStatus;
}

export interface CustomerOrder {
  order_id: number;
  customer_id: number; // FK -> CUSTOMERS
  created_by: number; // FK -> USERS
  order_date: string; // ISO yyyy-mm-dd
  required_date: string;
  status: OrderStatus;
  total_amount: number; // populated from ORDER_ITEMS; read orderTotal() for the derived value
}

export interface OrderItem {
  order_item_id: number;
  order_id: number; // FK -> CUSTOMER_ORDERS
  item_code: string;
  description: string;
  quantity: number;
  unit_price: number;
  line_total: number; // quantity * unit_price
}

export interface Bill {
  bill_id: number;
  order_id: number; // FK -> CUSTOMER_ORDERS (UNIQUE: one bill per order)
  bill_number: string; // UNIQUE
  bill_date: string;
  subtotal: number;
  tax_amount: number;
  total_amount: number;
  status: BillStatus;
}

export interface Payment {
  payment_id: number;
  bill_id: number; // FK -> BILLS
  payment_date: string;
  amount: number;
  method: string;
  reference_no: string;
  status: string; // value set not defined in the DRD
}

// Independent: no foreign keys.
export interface Supplier {
  supplier_id: number;
  supplier_name: string;
  contact_person: string;
  email: string;
  phone: string;
  address: string;
  status: RecordStatus;
}

export const ORDER_STATUSES: OrderStatus[] = ['NEW', 'CONFIRMED', 'PROCESSING', 'COMPLETED'];

// ---------------------------------------------------------------- ROLES / USERS
export const ROLES: Role[] = [
  { role_id: 1, role_name: 'Administrator', description: 'Manage user access, roles and basic configuration.' },
  { role_id: 2, role_name: 'Order operator', description: 'Create, validate, update and view customer orders.' },
  { role_id: 3, role_name: 'Billing / finance user', description: 'Generate bills and record or review payment entries.' },
  { role_id: 4, role_name: 'Supplier manager', description: 'Maintain supplier records and status.' },
];

export const USERS: User[] = [
  { user_id: 1, role_id: 1, full_name: 'Aarav Mehta', email: 'aarav.mehta@example.com', password_hash: 'mock-hash', status: 'ACTIVE' },
  { user_id: 2, role_id: 2, full_name: 'Neha Verma', email: 'neha.verma@example.com', password_hash: 'mock-hash', status: 'ACTIVE' },
  { user_id: 3, role_id: 3, full_name: 'Rohit Sinha', email: 'rohit.sinha@example.com', password_hash: 'mock-hash', status: 'ACTIVE' },
  { user_id: 4, role_id: 4, full_name: 'Pooja Nair', email: 'pooja.nair@example.com', password_hash: 'mock-hash', status: 'ACTIVE' },
];

// ---------------------------------------------------------------- CUSTOMERS
export const CUSTOMERS: Customer[] = [
  { customer_id: 1, customer_name: 'Apex Fabrications', email: 'orders@apexfab.example.com', phone: '+91 98100 11001', billing_address: 'Okhla Industrial Area, New Delhi', status: 'ACTIVE' },
  { customer_id: 2, customer_name: 'Sharma Auto Parts', email: 'accounts@sharmaauto.example.com', phone: '+91 98100 11002', billing_address: 'Sector 63, Noida', status: 'ACTIVE' },
  { customer_id: 3, customer_name: 'Bharat Steelworks', email: 'billing@bharatsteel.example.com', phone: '+91 98100 11003', billing_address: 'Sahibabad Industrial Area, Ghaziabad', status: 'ACTIVE' },
  { customer_id: 4, customer_name: 'Kumar Engineering', email: 'info@kumareng.example.com', phone: '+91 98100 11004', billing_address: 'Faridabad, Haryana', status: 'ACTIVE' },
  { customer_id: 5, customer_name: 'Nova Machine Tools', email: 'purchase@novamt.example.com', phone: '+91 98100 11005', billing_address: 'Manesar, Gurugram', status: 'ACTIVE' },
  { customer_id: 6, customer_name: 'Delhi Precision Co.', email: 'sales@delhiprecision.example.com', phone: '+91 98100 11006', billing_address: 'Mayapuri, New Delhi', status: 'ACTIVE' },
  { customer_id: 7, customer_name: 'Gupta Industries', email: 'gupta@guptaind.example.com', phone: '+91 98100 11007', billing_address: 'Bahadurgarh, Haryana', status: 'ACTIVE' },
  { customer_id: 8, customer_name: 'Rathi Components', email: 'rathi@rathicomp.example.com', phone: '+91 98100 11008', billing_address: 'Greater Noida', status: 'ACTIVE' },
];

// ---------------------------------------------------------------- ORDER_ITEMS
function item(
  order_item_id: number,
  order_id: number,
  item_code: string,
  description: string,
  quantity: number,
  unit_price: number,
): OrderItem {
  return { order_item_id, order_id, item_code, description, quantity, unit_price, line_total: quantity * unit_price };
}

export const ORDER_ITEMS: OrderItem[] = [
  item(1, 1001, 'GS-250', 'Gear shaft 250 mm', 40, 1200),
  item(2, 1001, 'BS-06', 'Bearing set', 20, 1420),
  item(3, 1002, 'FL-80', 'Flange plate', 60, 1500),
  item(4, 1002, 'BK-15', 'Mounting bracket', 100, 399),
  item(5, 1003, 'PN-40', 'Precision pin', 200, 271),
  item(6, 1004, 'MT-300', 'Machine table base', 5, 39000),
  item(7, 1004, 'CL-22', 'Clamp assembly', 50, 1046),
  item(8, 1005, 'HS-18', 'Hydraulic seal kit', 25, 1750),
  item(9, 1005, 'GK-09', 'Gasket pack', 50, 500),
  item(10, 1006, 'ST-500', 'Steel rod 500 mm', 300, 750),
  item(11, 1006, 'ST-120', 'Steel plate', 60, 1500),
  item(12, 1007, 'AP-77', 'Axle pin', 100, 425),
  item(13, 1007, 'BP-31', 'Brake plate', 50, 1000),
  item(14, 1008, 'FB-900', 'Fabricated frame', 8, 18000),
  item(15, 1008, 'WP-10', 'Weld plate', 40, 1000),
];

/** Order total = sum of the stored line totals of the order (DRD section 5). */
export function orderTotal(order_id: number): number {
  return ORDER_ITEMS.filter(i => i.order_id === order_id).reduce((sum, i) => sum + i.line_total, 0);
}

// ---------------------------------------------------------------- CUSTOMER_ORDERS
function order(
  order_id: number,
  customer_id: number,
  created_by: number,
  order_date: string,
  required_date: string,
  status: OrderStatus,
): CustomerOrder {
  return { order_id, customer_id, created_by, order_date, required_date, status, total_amount: orderTotal(order_id) };
}

export const CUSTOMER_ORDERS: CustomerOrder[] = [
  order(1001, 8, 2, '2026-09-15', '2026-10-06', 'COMPLETED'),
  order(1002, 7, 2, '2026-09-20', '2026-10-30', 'NEW'),
  order(1003, 6, 2, '2026-09-25', '2026-10-16', 'COMPLETED'),
  order(1004, 5, 2, '2026-09-28', '2026-10-19', 'PROCESSING'),
  order(1005, 4, 2, '2026-10-02', '2026-10-23', 'PROCESSING'),
  order(1006, 3, 2, '2026-10-04', '2026-10-25', 'COMPLETED'),
  order(1007, 2, 2, '2026-10-07', '2026-10-28', 'CONFIRMED'),
  order(1008, 1, 2, '2026-10-09', '2026-10-30', 'NEW'),
];

// ---------------------------------------------------------------- BILLS
// Tax policy is unconfirmed in the DRD, so tax_amount is 0 in the mock data.
function bill(bill_id: number, order_id: number, bill_number: string, bill_date: string, status: BillStatus): Bill {
  const subtotal = orderTotal(order_id);
  const tax_amount = 0;
  return { bill_id, order_id, bill_number, bill_date, subtotal, tax_amount, total_amount: subtotal + tax_amount, status };
}

export const BILLS: Bill[] = [
  bill(1, 1001, 'BILL-2026-0001', '2026-09-16', 'ISSUED'),
  bill(2, 1003, 'BILL-2026-0002', '2026-09-26', 'ISSUED'),
  bill(3, 1004, 'BILL-2026-0003', '2026-09-29', 'ISSUED'),
  bill(4, 1005, 'BILL-2026-0004', '2026-10-03', 'GENERATED'),
  bill(5, 1006, 'BILL-2026-0005', '2026-10-05', 'ISSUED'),
];

// ---------------------------------------------------------------- PAYMENTS
export const PAYMENTS: Payment[] = [
  { payment_id: 1, bill_id: 1, payment_date: '2026-09-18', amount: 76400, method: 'Bank transfer', reference_no: 'REF-0001', status: 'RECORDED' },
  { payment_id: 2, bill_id: 2, payment_date: '2026-09-27', amount: 54200, method: 'Cheque', reference_no: 'REF-0002', status: 'RECORDED' },
  { payment_id: 3, bill_id: 3, payment_date: '2026-10-01', amount: 100000, method: 'Bank transfer', reference_no: 'REF-0003', status: 'RECORDED' },
  { payment_id: 4, bill_id: 5, payment_date: '2026-10-06', amount: 200000, method: 'Bank transfer', reference_no: 'REF-0004', status: 'RECORDED' },
  { payment_id: 5, bill_id: 5, payment_date: '2026-10-10', amount: 115000, method: 'Cheque', reference_no: 'REF-0005', status: 'RECORDED' },
];

// ---------------------------------------------------------------- SUPPLIERS (independent)
export const SUPPLIERS: Supplier[] = [
  { supplier_id: 1, supplier_name: 'Hindustan Alloys', contact_person: 'Vikram Anand', email: 'vikram@hindalloys.example.com', phone: '+91 98200 22001', address: 'Sector 5, Bhiwadi', status: 'ACTIVE' },
  { supplier_id: 2, supplier_name: 'Metro Fasteners', contact_person: 'Sunita Rao', email: 'sunita@metrofast.example.com', phone: '+91 98200 22002', address: 'Wazirpur, New Delhi', status: 'ACTIVE' },
  { supplier_id: 3, supplier_name: 'Shakti Castings', contact_person: 'Manoj Tiwari', email: 'manoj@shakticast.example.com', phone: '+91 98200 22003', address: 'Ballabgarh, Haryana', status: 'ACTIVE' },
  { supplier_id: 4, supplier_name: 'Prime Bearings', contact_person: 'Anita Joshi', email: 'anita@primebear.example.com', phone: '+91 98200 22004', address: 'Loni, Ghaziabad', status: 'ACTIVE' },
  { supplier_id: 5, supplier_name: 'Omega Packaging', contact_person: 'Karan Malhotra', email: 'karan@omegapack.example.com', phone: '+91 98200 22005', address: 'Kundli, Sonipat', status: 'ACTIVE' },
  { supplier_id: 6, supplier_name: 'Vishal Coatings', contact_person: 'Deepak Chauhan', email: 'deepak@vishalcoat.example.com', phone: '+91 98200 22006', address: 'Dadri, Greater Noida', status: 'INACTIVE' },
];

// ---------------------------------------------------------------- Derived values
export function billForOrder(order_id: number): Bill | undefined {
  return BILLS.find(b => b.order_id === order_id);
}

/** DRD: payments are positive amounts recorded against a bill. */
export function isValidPayment(p: Payment): boolean {
  return p.amount > 0;
}

/** Paid amount = sum of valid payments linked to the bill. */
export function paidAmount(b: Bill): number {
  return PAYMENTS.filter(p => p.bill_id === b.bill_id && isValidPayment(p)).reduce((sum, p) => sum + p.amount, 0);
}

/** Amount due = bill total minus valid payments. */
export function amountDue(b: Bill): number {
  return b.total_amount - paidAmount(b);
}

export function paymentStatusOf(b: Bill): AggregatePaymentStatus {
  const paid = paidAmount(b);
  if (paid <= 0) return 'PENDING';
  return paid >= b.total_amount ? 'PAID' : 'PARTIALLY PAID';
}

export function inr(value: number): string {
  return new Intl.NumberFormat('en-IN', {
    style: 'currency',
    currency: 'INR',
    maximumFractionDigits: 0,
  }).format(value);
}

// ---------------------------------------------------------------- Consistency check
// Fails fast if the mock data breaks the DRD keys, unique rules or derived values.
function validateMockData(): void {
  const errors: string[] = [];
  const check = (ok: boolean, msg: string) => {
    if (!ok) errors.push(msg);
  };

  USERS.forEach(u => check(ROLES.some(r => r.role_id === u.role_id), `USERS ${u.user_id}: unknown role_id`));

  CUSTOMER_ORDERS.forEach(o => {
    check(CUSTOMERS.some(c => c.customer_id === o.customer_id), `ORDER ${o.order_id}: unknown customer_id`);
    check(USERS.some(u => u.user_id === o.created_by), `ORDER ${o.order_id}: unknown created_by`);
    check(ORDER_ITEMS.some(i => i.order_id === o.order_id), `ORDER ${o.order_id}: needs at least one item`);
    check(o.total_amount === orderTotal(o.order_id), `ORDER ${o.order_id}: total_amount differs from item sum`);
  });

  ORDER_ITEMS.forEach(i => {
    check(CUSTOMER_ORDERS.some(o => o.order_id === i.order_id), `ITEM ${i.order_item_id}: unknown order_id`);
    check(i.quantity > 0 && i.unit_price >= 0, `ITEM ${i.order_item_id}: quantity must be > 0 and unit_price >= 0`);
    check(i.line_total === i.quantity * i.unit_price, `ITEM ${i.order_item_id}: line_total != quantity * unit_price`);
  });

  BILLS.forEach(b => {
    check(CUSTOMER_ORDERS.some(o => o.order_id === b.order_id), `BILL ${b.bill_id}: unknown order_id`);
    check(BILLS.filter(x => x.order_id === b.order_id).length === 1, `BILL ${b.bill_id}: order has more than one bill`);
    check(BILLS.filter(x => x.bill_number === b.bill_number).length === 1, `BILL ${b.bill_id}: duplicate bill_number`);
    check(b.subtotal === orderTotal(b.order_id), `BILL ${b.bill_id}: subtotal differs from order total`);
    check(paidAmount(b) <= b.total_amount, `BILL ${b.bill_id}: payments exceed bill total`);
  });

  PAYMENTS.forEach(p => {
    check(BILLS.some(b => b.bill_id === p.bill_id), `PAYMENT ${p.payment_id}: unknown bill_id`);
    check(p.amount >= 0, `PAYMENT ${p.payment_id}: amount must be non-negative`);
  });

  if (errors.length > 0) {
    throw new Error('Invalid mock data:\n' + errors.join('\n'));
  }
}

validateMockData();