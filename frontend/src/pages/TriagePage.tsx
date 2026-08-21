import { useEffect, useState } from 'react';
import { api } from '../api/client';
import { SeverityBadge } from '../components/SeverityBadge';
import type { AdmissionResult, PatientDto, RegionDto, Severity } from '../types';
import '../styles/panels.css';
import './TriagePage.css';

const SEVERITIES: Severity[] = ['LOW', 'MEDIUM', 'HIGH'];

export function TriagePage() {
  const [waiting, setWaiting] = useState<PatientDto[]>([]);
  const [regions, setRegions] = useState<RegionDto[]>([]);
  const [selectedRegion, setSelectedRegion] = useState('');
  const [result, setResult] = useState<AdmissionResult | null>(null);
  const [admitting, setAdmitting] = useState(false);
  const [error, setError] = useState<string | null>(null);

  // manual entry form state
  const [name, setName] = useState('');
  const [severity, setSeverity] = useState<Severity>('MEDIUM');
  const [preferredHospital, setPreferredHospital] = useState('');
  const [submitting, setSubmitting] = useState(false);

  // bulk entry form state
  const [bulkCounts, setBulkCounts] = useState<Record<Severity, number>>({ LOW: 0, MEDIUM: 0, HIGH: 0 });
  const [bulkSubmitting, setBulkSubmitting] = useState(false);

  async function refreshWaiting() {
    const patients = await api.getPatients('WAITING');
    setWaiting(patients);
  }

  useEffect(() => {
    refreshWaiting().catch((err) => setError(err.message));
    api.getRegions().then((rs) => {
      setRegions(rs);
      if (rs.length > 0) setSelectedRegion(rs[0].name);
    });
  }, []);

  async function handleManualSubmit(e: React.FormEvent) {
    e.preventDefault();
    if (!name.trim()) return;
    setSubmitting(true);
    setError(null);
    try {
      await api.registerPatient(name.trim(), severity, preferredHospital.trim());
      setName('');
      setPreferredHospital('');
      await refreshWaiting();
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Failed to register patient');
    } finally {
      setSubmitting(false);
    }
  }

  async function handleBulkSubmit() {
    const severities: Severity[] = [];
    for (const s of SEVERITIES) {
      severities.push(...Array(bulkCounts[s]).fill(s));
    }
    if (severities.length === 0) return;

    setBulkSubmitting(true);
    setError(null);
    try {
      await api.registerBulk(severities);
      setBulkCounts({ LOW: 0, MEDIUM: 0, HIGH: 0 });
      await refreshWaiting();
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Failed to register bulk intake');
    } finally {
      setBulkSubmitting(false);
    }
  }

  async function handleAdmit() {
    if (!selectedRegion) return;
    setAdmitting(true);
    setError(null);
    setResult(null);
    try {
      const res = await api.admitPatients(selectedRegion);
      setResult(res);
      await refreshWaiting();
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Admission run failed');
    } finally {
      setAdmitting(false);
    }
  }

  const sortedWaiting = [...waiting].sort(
    (a, b) => SEVERITIES.indexOf(b.severity) - SEVERITIES.indexOf(a.severity)
  );

  return (
    <div className="triage-page">
      <div className="triage-page__intake panel">
        <div className="panel-heading">
          <div>
            <h2>Patient Intake</h2>
            <p className="panel-heading__sub">Register patients as they arrive</p>
          </div>
        </div>

        <form onSubmit={handleManualSubmit} className="intake-form">
          <div className="field">
            <label>Name</label>
            <input value={name} onChange={(e) => setName(e.target.value)} placeholder="Full name" required />
          </div>
          <div className="field">
            <label>Severity</label>
            <select value={severity} onChange={(e) => setSeverity(e.target.value as Severity)}>
              {SEVERITIES.map((s) => (
                <option key={s} value={s}>
                  {s}
                </option>
              ))}
            </select>
          </div>
          <div className="field">
            <label>Preferred hospital (optional)</label>
            <input
              value={preferredHospital}
              onChange={(e) => setPreferredHospital(e.target.value)}
              placeholder="e.g. Dehradun General"
            />
          </div>
          <button className="btn btn--primary" type="submit" disabled={submitting}>
            {submitting ? 'Registering…' : 'Register patient'}
          </button>
        </form>

        <div className="intake-divider">
          <span>or bulk intake</span>
        </div>

        <div className="bulk-form">
          {SEVERITIES.map((s) => (
            <div className="bulk-form__row" key={s}>
              <SeverityBadge severity={s} />
              <input
                type="number"
                min={0}
                value={bulkCounts[s]}
                onChange={(e) =>
                  setBulkCounts((prev) => ({ ...prev, [s]: Math.max(0, Number(e.target.value)) }))
                }
              />
            </div>
          ))}
          <button className="btn btn--ghost" onClick={handleBulkSubmit} disabled={bulkSubmitting}>
            {bulkSubmitting ? 'Adding…' : 'Add bulk patients'}
          </button>
        </div>

        {error && <div className="banner-error">{error}</div>}
      </div>

      <div className="triage-page__queue panel">
        <div className="panel-heading">
          <div>
            <h2>Triage Queue</h2>
            <p className="panel-heading__sub">{waiting.length} patient(s) waiting, highest severity first</p>
          </div>
        </div>

        {sortedWaiting.length === 0 ? (
          <div className="empty-state">No patients waiting.</div>
        ) : (
          <ul className="queue-list">
            {sortedWaiting.map((p) => (
              <li key={p.id} className="queue-list__item">
                <span className="queue-list__name">{p.name}</span>
                <SeverityBadge severity={p.severity} />
              </li>
            ))}
          </ul>
        )}

        <div className="admit-controls">
          <div className="field">
            <label>Disaster region</label>
            <select value={selectedRegion} onChange={(e) => setSelectedRegion(e.target.value)}>
              {regions.map((r) => (
                <option key={r.name} value={r.name}>
                  {r.name}
                </option>
              ))}
            </select>
          </div>
          <button className="btn btn--primary" onClick={handleAdmit} disabled={admitting || waiting.length === 0}>
            {admitting ? 'Running admission…' : 'Run admission'}
          </button>
        </div>

        {result && (
          <div className="admission-result">
            <section>
              <h3 className="admission-result__title admission-result__title--good">
                Admitted ({result.admitted.length})
              </h3>
              <ul className="result-list">
                {result.admitted.map((p) => (
                  <li key={p.id}>
                    <span>{p.name}</span>
                    <span className="result-list__detail">
                      {p.assignedHospitalName} · {p.admittedRegion}
                    </span>
                  </li>
                ))}
              </ul>
            </section>
            {result.unassigned.length > 0 && (
              <section>
                <h3 className="admission-result__title admission-result__title--bad">
                  Unassigned ({result.unassigned.length})
                </h3>
                <ul className="result-list">
                  {result.unassigned.map((p) => (
                    <li key={p.id}>
                      <span>{p.name}</span>
                      <SeverityBadge severity={p.severity} />
                    </li>
                  ))}
                </ul>
              </section>
            )}
          </div>
        )}
      </div>
    </div>
  );
}
