import type { Severity } from '../types';
import './SeverityBadge.css';

const LABELS: Record<Severity, string> = {
  LOW: 'Low',
  MEDIUM: 'Medium',
  HIGH: 'Critical',
};

export function SeverityBadge({ severity }: { severity: Severity }) {
  return (
    <span className={`severity-badge severity-badge--${severity.toLowerCase()}`}>
      <span className="severity-badge__dot" aria-hidden="true" />
      {LABELS[severity]}
    </span>
  );
}
