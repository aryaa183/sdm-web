import { useEffect, useState } from 'react';
import { api } from '../api/client';
import { SeverityBadge } from '../components/SeverityBadge';
import type { BillDto, PatientDto } from '../types';
import '../styles/panels.css';
import './BillingPage.css';

const formatInr = (value: number) =>
  new Intl.NumberFormat('en-IN', { style: 'currency', currency: 'INR', maximumFractionDigits: 2 }).format(value);

export function BillingPage() {
  const [admitted, setAdmitted] = useState<PatientDto[]>([]);
  const [selectedPatient, setSelectedPatient] = useState<PatientDto | null>(null);
  const [bill, setBill] = useState<BillDto | null>(null);
  const [loadingBill, setLoadingBill] = useState(false);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    api.getPatients('ADMITTED').then(setAdmitted).catch((err) => setError(err.message));
  }, []);

  async function handleSelect(patient: PatientDto) {
    setSelectedPatient(patient);
    setBill(null);
    setLoadingBill(true);
    setError(null);
    try {
      const result = await api.generateBill(patient.id);
      setBill(result);
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Failed to generate bill');
    } finally {
      setLoadingBill(false);
    }
  }

  return (
    <div className="billing-page">
      <div className="panel billing-page__list">
        <div className="panel-heading">
          <div>
            <h2>Admitted Patients</h2>
            <p className="panel-heading__sub">Select a patient to generate their bill</p>
          </div>
        </div>

        {admitted.length === 0 ? (
          <div className="empty-state">No admitted patients yet — run admission on the Triage page first.</div>
        ) : (
          <ul className="billing-list">
            {admitted.map((p) => (
              <li
                key={p.id}
                className={`billing-list__item ${selectedPatient?.id === p.id ? 'billing-list__item--active' : ''}`}
                onClick={() => handleSelect(p)}
              >
                <div>
                  <span className="billing-list__name">{p.name}</span>
                  <span className="billing-list__hospital">{p.assignedHospitalName}</span>
                </div>
                <SeverityBadge severity={p.severity} />
              </li>
            ))}
          </ul>
        )}

        {error && <div className="banner-error">{error}</div>}
      </div>

      <div className="panel billing-page__receipt">
        <div className="panel-heading">
          <div>
            <h2>Bill</h2>
            <p className="panel-heading__sub">
              {selectedPatient ? selectedPatient.name : 'No patient selected'}
            </p>
          </div>
        </div>

        {loadingBill && <div className="empty-state">Generating bill…</div>}

        {!loadingBill && bill && (
          <div className="receipt">
            <div className="receipt__row">
              <span>Base treatment cost</span>
              <span>{formatInr(bill.baseCost)}</span>
            </div>
            <div className="receipt__row">
              <span>GST (18%)</span>
              <span>{formatInr(bill.gst)}</span>
            </div>
            <div className="receipt__row">
              <span>Service fee (5%)</span>
              <span>{formatInr(bill.serviceFee)}</span>
            </div>
            <div className="receipt__divider" />
            <div className="receipt__row receipt__row--total">
              <span>Total</span>
              <span>{formatInr(bill.total)}</span>
            </div>
          </div>
        )}

        {!loadingBill && !bill && (
          <div className="empty-state">Select an admitted patient to generate their bill.</div>
        )}
      </div>
    </div>
  );
}
