import React, { useEffect, useState } from 'react';
import { api, STAGES } from '../api.js';

export default function Dashboard() {
  const [m, setM] = useState(null);
  const [err, setErr] = useState('');

  useEffect(() => {
    api.metrics().then(setM).catch((e) => setErr(e.message));
  }, []);

  if (err) return <p className="error">{err}</p>;
  if (!m) return <p>Loading…</p>;
  const max = Math.max(1, ...Object.values(m.byStage));

  return (
    <>
      <div className="cards">
        <Card label="Total candidates" value={m.totalCandidates} />
        <Card label="New (last 7 days)" value={m.newLast7Days} />
        <Card label="Hired" value={m.hired} />
        <Card label="Hire rate" value={`${m.hireRatePercent}%`} />
        <Card label="Avg days to hire" value={m.avgDaysToHire ?? '—'} />
      </div>
      <div className="grid2">
        <section className="panel">
          <h3>Pipeline</h3>
          {STAGES.map((s) => (
            <div className="bar-row" key={s}>
              <span className="bar-label">{s}</span>
              <div className="bar-track">
                <div className={`bar-fill ${s}`} style={{ width: `${(m.byStage[s] / max) * 100}%` }} />
              </div>
              <span className="bar-count">{m.byStage[s]}</span>
            </div>
          ))}
        </section>
        <section className="panel">
          <h3>Top positions</h3>
          {m.topPositions.length === 0 && <p className="muted">No data yet</p>}
          <ul className="plain">
            {m.topPositions.map((p) => (
              <li key={p.position}><span>{p.position}</span><b>{p.count}</b></li>
            ))}
          </ul>
        </section>
      </div>
    </>
  );
}

const Card = ({ label, value }) => (
  <div className="card"><div className="card-value">{value}</div><div className="muted">{label}</div></div>
);
