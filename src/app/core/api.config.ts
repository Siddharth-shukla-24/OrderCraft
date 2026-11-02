export const API_BASE_URL = 'http://localhost:8080/api';

export function extractErrorMessage(err: any): string {
  if (err?.error?.message) {
    return err.error.message;
  }
  if (err?.error?.validationErrors) {
    const vals = Object.values(err.error.validationErrors);
    if (vals.length > 0) return vals.join(', ');
  }
  if (typeof err?.error === 'string' && err.error.trim().length > 0) {
    return err.error;
  }
  if (err?.message) {
    return err.message;
  }
  return 'A server communication error occurred';
}
