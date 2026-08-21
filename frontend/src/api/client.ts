import type {
  AdmissionResult,
  AlertResponse,
  BillDto,
  PatientDto,
  RegionDto,
  Severity,
  ApiError,
} from '../types';

const BASE_URL = import.meta.env.VITE_API_BASE_URL ?? 'http://localhost:8080';

async function request<T>(path: string, options?: RequestInit): Promise<T> {
  const res = await fetch(`${BASE_URL}${path}`, {
    headers: { 'Content-Type': 'application/json' },
    ...options,
  });

  if (!res.ok) {
    let message = `Request failed (${res.status})`;
    try {
      const body: ApiError = await res.json();
      message = body.messages?.join(', ') || message;
    } catch {
      // response wasn't JSON — fall back to the generic message above
    }
    throw new Error(message);
  }

  // 204 No Content etc.
  const text = await res.text();
  return text ? (JSON.parse(text) as T) : (undefined as T);
}

export const api = {
  getRegions: () => request<RegionDto[]>('/api/regions'),

  alertRegion: (regionName: string) =>
    request<AlertResponse>(`/api/regions/${encodeURIComponent(regionName)}/alert`, {
      method: 'POST',
    }),

  getPatients: (status?: string) =>
    request<PatientDto[]>(`/api/patients${status ? `?status=${status}` : ''}`),

  registerPatient: (name: string, severity: Severity, preferredHospital: string) =>
    request<PatientDto>('/api/patients', {
      method: 'POST',
      body: JSON.stringify({ name, severity, preferredHospital }),
    }),

  registerBulk: (severities: Severity[]) =>
    request<PatientDto[]>('/api/patients/bulk', {
      method: 'POST',
      body: JSON.stringify({ severities }),
    }),

  admitPatients: (region: string) =>
    request<AdmissionResult>(`/api/patients/admit?region=${encodeURIComponent(region)}`, {
      method: 'POST',
    }),

  generateBill: (patientId: number) =>
    request<BillDto>(`/api/patients/${patientId}/bill`, { method: 'POST' }),
};
