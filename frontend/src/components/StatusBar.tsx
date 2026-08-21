import { useEffect, useState } from 'react';
import { NavLink } from 'react-router-dom';
import './StatusBar.css';

export function StatusBar() {
  const [now, setNow] = useState(new Date());

  useEffect(() => {
    const id = setInterval(() => setNow(new Date()), 1000);
    return () => clearInterval(id);
  }, []);

  const time = now.toLocaleTimeString('en-IN', { hour12: false });
  const date = now.toLocaleDateString('en-IN', { day: '2-digit', month: 'short', year: 'numeric' });

  return (
    <header className="status-bar">
      <div className="status-bar__brand">
        <span className="status-bar__mark" aria-hidden="true" />
        <div>
          <h1 className="status-bar__title">Field Ops</h1>
          <p className="status-bar__subtitle">Disaster Response Console</p>
        </div>
      </div>

      <nav className="status-bar__nav">
        <NavLink to="/" end className={({ isActive }) => (isActive ? 'active' : '')}>
          Network
        </NavLink>
        <NavLink to="/triage" className={({ isActive }) => (isActive ? 'active' : '')}>
          Triage
        </NavLink>
        <NavLink to="/billing" className={({ isActive }) => (isActive ? 'active' : '')}>
          Billing
        </NavLink>
      </nav>

      <div className="status-bar__system">
        <span className="status-bar__pill">
          <span className="status-bar__pulse" aria-hidden="true" />
          System Operational
        </span>
        <span className="status-bar__clock">
          {date} · {time}
        </span>
      </div>
    </header>
  );
}
