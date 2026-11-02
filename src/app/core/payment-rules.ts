import { AggregatePaymentStatus } from './mock-data';

/**
 * ASSUMPTION from the DRD (section 5): "Payment must not exceed amount due".
 * Overpayment / refund policy needs trainer confirmation (section 9).
 * Change ONLY this flag/function when the policy is confirmed.
 */
export const ALLOW_OVERPAYMENT = false;

/** Compare money at 2-decimal precision (rounding policy is also unconfirmed). */
export function cents(value: number): number {
  return Math.round(value * 100);
}

/** Error message for an invalid payment amount, or null if the amount is acceptable. */
export function paymentAmountError(amount: number, amountDue: number): string | null {
  if (!(amount > 0)) return 'Amount must be greater than 0';
  if (!ALLOW_OVERPAYMENT && cents(amount) > cents(amountDue)) return 'Amount cannot exceed the amount due';
  return null;
}

/** CSS class suffix for an aggregate payment status ('none' = order has no bill). */
export function paymentClass(status: AggregatePaymentStatus | null): string {
  if (status === 'PAID') return 'paid';
  if (status === 'PARTIALLY PAID') return 'partial';
  if (status === 'PENDING') return 'pending';
  return 'none';
}