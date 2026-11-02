import { CustomerOrder } from './mock-data';

/**
 * MENTOR CONFIRMATION REQUIRED (DRD sections 4, 7, 9):
 * The DRD does not define which orders are eligible for billing
 * (e.g. required order status, minimum lifecycle stage).
 * Until confirmed, this prototype places NO status restriction.
 * The DRD-backed checks (order exists, has line items, has no bill yet)
 * live in OrderStore.billBlocker().
 * Change ONLY this function when the rule is confirmed.
 */
export function isOrderEligibleForBill(_order: CustomerOrder): boolean {
  return true;
}

export const BILL_ELIGIBILITY_NOTE =
  'Bill eligibility is pending mentor confirmation. For now, any order that has line items and no bill can be billed.';

/** Tax policy is unconfirmed (DRD sections 4 and 9): tax is fixed at 0 in the prototype. */
export const PROTOTYPE_TAX_AMOUNT = 0;