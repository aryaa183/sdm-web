export type Severity = 'LOW' | 'MEDIUM' | 'HIGH';

export type PatientStatus = 'WAITING' | 'ADMITTED' | 'UNASSIGNED';

export interface HospitalDto {
  id: number;
  name: string;
  regionName: string;
  totalBeds: number;
  bedsAvailable: number;
}

export interface RegionDto {
  id: number;
  name: string;
  connectedRegions: string[];
  hospitals: HospitalDto[];
}

export interface AlertResponse {
  region: string;
  localHospitals: HospitalDto[];
  connectedRegionHospitals: Record<string, HospitalDto[]>;
}

export interface PatientDto {
  id: number;
  name: string;
  severity: Severity;
  preferredHospital: string | null;
  status: PatientStatus;
  assignedHospitalName: string | null;
  admittedRegion: string | null;
}

export interface AdmissionResult {
  disasterRegion: string;
  admitted: PatientDto[];
  unassigned: PatientDto[];
}

export interface BillDto {
  patientId: number;
  patientName: string;
  severity: Severity;
  baseCost: number;
  gst: number;
  serviceFee: number;
  total: number;
}

export interface ApiError {
  timestamp: string;
  status: number;
  error: string;
  messages: string[];
}
