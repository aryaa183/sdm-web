import { useEffect, useMemo, useState } from 'react';
import { api } from '../api/client';
import type { AlertResponse, RegionDto } from '../types';
import './NetworkPage.css';

interface LaidOutRegion extends RegionDto {
  x: number;
  y: number;
}

const RADIUS = 190;
const CENTER = 220;

function layoutRegions(regions: RegionDto[]): LaidOutRegion[] {
  return regions.map((region, i) => {
    const angle = (i / regions.length) * Math.PI * 2 - Math.PI / 2;
    return {
      ...region,
      x: CENTER + RADIUS * Math.cos(angle),
      y: CENTER + RADIUS * Math.sin(angle),
    };
  });
}

function occupancy(region: RegionDto) {
  const total = region.hospitals.reduce((sum, h) => sum + h.totalBeds, 0);
  const available = region.hospitals.reduce((sum, h) => sum + h.bedsAvailable, 0);
  return { total, available, filled: total - available };
}

export function NetworkPage() {
  const [regions, setRegions] = useState<RegionDto[]>([]);
  const [selected, setSelected] = useState<string | null>(null);
  const [alert, setAlert] = useState<AlertResponse | null>(null);
  const [loading, setLoading] = useState(true);
  const [alerting, setAlerting] = useState(false);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    api
      .getRegions()
      .then((data) => {
        setRegions(data);
        setLoading(false);
      })
      .catch((err) => {
        setError(err.message);
        setLoading(false);
      });
  }, []);

  const laidOut = useMemo(() => layoutRegions(regions), [regions]);
  const byName = useMemo(() => new Map(laidOut.map((r) => [r.name, r])), [laidOut]);

  const edges = useMemo(() => {
    const seen = new Set<string>();
    const list: { from: LaidOutRegion; to: LaidOutRegion }[] = [];
    for (const region of laidOut) {
      for (const connectedName of region.connectedRegions) {
        const key = [region.name, connectedName].sort().join('::');
        if (seen.has(key)) continue;
        seen.add(key);
        const target = byName.get(connectedName);
        if (target) list.push({ from: region, to: target });
      }
    }
    return list;
  }, [laidOut, byName]);

  async function handleSelect(regionName: string) {
    setSelected(regionName);
    setAlerting(true);
    setAlert(null);
    setError(null);
    try {
      const result = await api.alertRegion(regionName);
      setAlert(result);
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Failed to alert region');
    } finally {
      setAlerting(false);
    }
  }

  const selectedRegion = selected ? byName.get(selected) : null;

  return (
    <div className="network-page">
      <div className="network-page__map-panel">
        <div className="panel-heading">
          <div>
            <h2>Region Network</h2>
            <p className="panel-heading__sub">Select the disaster region to broadcast an alert</p>
          </div>
        </div>

        {loading ? (
          <div className="network-page__empty">Loading network…</div>
        ) : error && regions.length === 0 ? (
          <div className="network-page__empty network-page__empty--error">
            Couldn't reach the backend. Is it running on port 8080? ({error})
          </div>
        ) : (
          <svg viewBox="0 0 440 440" className="network-svg" role="img" aria-label="Region connection map">
            <defs>
              <filter id="glow">
                <feGaussianBlur stdDeviation="3.2" result="blur" />
                <feMerge>
                  <feMergeNode in="blur" />
                  <feMergeNode in="SourceGraphic" />
                </feMerge>
              </filter>
            </defs>

            {edges.map((edge, i) => (
              <line
                key={i}
                x1={edge.from.x}
                y1={edge.from.y}
                x2={edge.to.x}
                y2={edge.to.y}
                className="network-svg__edge"
              />
            ))}

            {laidOut.map((region) => {
              const { total, filled } = occupancy(region);
              const fillRatio = total > 0 ? filled / total : 0;
              const isSelected = region.name === selected;
              const r = 34;
              const circumference = 2 * Math.PI * r;

              return (
                <g
                  key={region.name}
                  transform={`translate(${region.x}, ${region.y})`}
                  className={`network-node ${isSelected ? 'network-node--selected' : ''}`}
                  onClick={() => handleSelect(region.name)}
                  role="button"
                  tabIndex={0}
                  aria-label={`Alert ${region.name}`}
                  onKeyDown={(e) => e.key === 'Enter' && handleSelect(region.name)}
                >
                  <circle r={r + 10} className="network-node__halo" />
                  <circle r={r} className="network-node__track" />
                  <circle
                    r={r}
                    className="network-node__fill"
                    style={{
                      strokeDasharray: circumference,
                      strokeDashoffset: circumference * (1 - fillRatio),
                    }}
                  />
                  <text className="network-node__label" y={r + 22}>
                    {region.name}
                  </text>
                  <text className="network-node__count" y={5}>
                    {total - filled}
                  </text>
                  <text className="network-node__count-label" y={19}>
                    free
                  </text>
                </g>
              );
            })}
          </svg>
        )}

        <div className="network-page__legend">
          <span><i className="dot dot--teal" /> Bed occupancy ring</span>
          <span><i className="dot dot--line" /> Overflow connection</span>
        </div>
      </div>

      <div className="network-page__side-panel">
        <div className="panel-heading">
          <h2>{selectedRegion ? selectedRegion.name : 'Select a region'}</h2>
          <p className="panel-heading__sub">
            {selectedRegion ? 'Alert status & hospital readout' : 'Click a node on the map to begin'}
          </p>
        </div>

        {alerting && <div className="network-page__empty">Broadcasting alert…</div>}

        {!alerting && alert && (
          <div className="alert-result">
            <section>
              <h3 className="alert-result__section-title">In-region hospitals</h3>
              <ul className="hospital-list">
                {alert.localHospitals.map((h) => (
                  <li key={h.id}>
                    <span>{h.name}</span>
                    <span className="hospital-list__beds">
                      {h.bedsAvailable}/{h.totalBeds} beds
                    </span>
                  </li>
                ))}
              </ul>
            </section>

            {Object.entries(alert.connectedRegionHospitals).map(([regionName, hospitals]) => (
              <section key={regionName}>
                <h3 className="alert-result__section-title alert-result__section-title--muted">
                  Overflow — {regionName}
                </h3>
                <ul className="hospital-list">
                  {hospitals.map((h) => (
                    <li key={h.id}>
                      <span>{h.name}</span>
                      <span className="hospital-list__beds">
                        {h.bedsAvailable}/{h.totalBeds} beds
                      </span>
                    </li>
                  ))}
                </ul>
              </section>
            ))}
          </div>
        )}

        {!alerting && !alert && !selectedRegion && (
          <div className="network-page__empty">No region selected yet.</div>
        )}
      </div>
    </div>
  );
}
