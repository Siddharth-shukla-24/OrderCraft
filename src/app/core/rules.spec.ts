import { PROTOTYPE_TAX_AMOUNT, isOrderEligibleForBill } from './bill-rules';
import { paymentAmountError, paymentClass, cents } from './payment-rules';
import { CUSTOMER_ORDERS, orderTotal } from './mock-data';

describe('OrderCraft Core Business Rules', () => {
  it('should enforce zero tax policy', () => {
    expect(PROTOTYPE_TAX_AMOUNT).toBe(0);
  });

  it('should consider valid orders eligible for billing', () => {
    expect(isOrderEligibleForBill(CUSTOMER_ORDERS[0])).toBeTrue();
  });

  it('should validate positive payment amount', () => {
    expect(paymentAmountError(0, 1000)).toBe('Amount must be greater than 0');
    expect(paymentAmountError(-50, 1000)).toBe('Amount must be greater than 0');
  });

  it('should reject payment exceeding amount due', () => {
    expect(paymentAmountError(1001, 1000)).toBe('Amount cannot exceed the amount due');
    expect(paymentAmountError(1000, 1000)).toBeNull();
    expect(paymentAmountError(500, 1000)).toBeNull();
  });

  it('should map aggregate payment statuses to css classes', () => {
    expect(paymentClass('PAID')).toBe('paid');
    expect(paymentClass('PARTIALLY PAID')).toBe('partial');
    expect(paymentClass('PENDING')).toBe('pending');
    expect(paymentClass(null)).toBe('none');
  });

  it('should compute order total from line items correctly', () => {
    const total = orderTotal(1001);
    expect(total).toBe(40 * 1200 + 20 * 1420); // 48000 + 28400 = 76400
  });
});
