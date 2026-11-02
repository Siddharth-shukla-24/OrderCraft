import { Routes } from '@angular/router';
import { DashboardComponent } from './pages/dashboard/dashboard.component';
import { OrderListComponent } from './pages/orders/order-list.component';
import { OrderFormComponent } from './pages/orders/order-form.component';
import { OrderDetailComponent } from './pages/orders/order-detail.component';
import { BillListComponent } from './pages/bills/bill-list.component';
import { BillGenerateComponent } from './pages/bills/bill-generate.component';
import { BillDetailComponent } from './pages/bills/bill-detail.component';
import { TrackingListComponent } from './pages/tracking/tracking-list.component';
import { TrackingDetailComponent } from './pages/tracking/tracking-detail.component';
import { SupplierListComponent } from './pages/suppliers/supplier-list.component';
import { SupplierFormComponent } from './pages/suppliers/supplier-form.component';
import { SupplierDetailComponent } from './pages/suppliers/supplier-detail.component';

export const routes: Routes = [
  { path: '', pathMatch: 'full', redirectTo: 'dashboard' },
  { path: 'dashboard', component: DashboardComponent, title: 'Dashboard | OrderCraft' },
  { path: 'orders', component: OrderListComponent, title: 'Order Processing | OrderCraft' },
  { path: 'orders/new', component: OrderFormComponent, title: 'New Order | OrderCraft' },
  { path: 'orders/:id', component: OrderDetailComponent, title: 'Order Details | OrderCraft' },
  { path: 'orders/:id/edit', component: OrderFormComponent, title: 'Edit Order | OrderCraft' },
  { path: 'billing', component: BillListComponent, title: 'Bill Generation | OrderCraft' },
  { path: 'billing/new', component: BillGenerateComponent, title: 'Generate Bill | OrderCraft' },
  { path: 'billing/:id', component: BillDetailComponent, title: 'Bill Details | OrderCraft' },
  { path: 'tracking', component: TrackingListComponent, title: 'Order & Payment Tracking | OrderCraft' },
  { path: 'tracking/:orderId', component: TrackingDetailComponent, title: 'Order Tracking | OrderCraft' },
  { path: 'suppliers', component: SupplierListComponent, title: 'Supplier Management | OrderCraft' },
  { path: 'suppliers/new', component: SupplierFormComponent, title: 'Add Supplier | OrderCraft' },
  { path: 'suppliers/:id', component: SupplierDetailComponent, title: 'Supplier Details | OrderCraft' },
  { path: 'suppliers/:id/edit', component: SupplierFormComponent, title: 'Edit Supplier | OrderCraft' },
  { path: '**', redirectTo: 'dashboard' },
];