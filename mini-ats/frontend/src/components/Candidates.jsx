import React, { useEffect, useState, useCallback } from 'react';
import { api, STAGES } from '../api.js';
import CandidateForm from './CandidateForm.jsx';
import CandidateDetail from './CandidateDetail.jsx';

export default function Candidates() {
  const [filters, setFilters] = useState({ q: '', stage: '', minExperience: '' });
  const [q, setQ] = useState('');
  const [page, setPage] = useState(0);
  const [sort, setSort] = useState({ sortBy: 'createdAt', dir: 'desc' });
  const [data, setData] = useState({ items: [], totalPages: 0, totalItems: 0 });
  const [err, setErr] = useState('');
  const [showForm, setShowForm] = useState(false);
  const [selected, setSelected] = useState(null);

  // debounce search box
  useEffect(() => {
    const t = setTimeout(() => { setFilters((f) => ({ ...f, q })); setPage(0); }, 300);
    return () => clearTimeout(t);
  }, [q]);

  const load = useCallback(() => {
    api.list({ ...filters, ...sort, page, size: 8 })
      .then((d) => { setData(d); setErr(''); })
      .catch((e) => setErr(e.message));
  }, [filters, sort, page]);

  useEffect(load, [load]);

  const toggleSort = (field) =>
    setSort((s) => ({ sortBy: field, dir: s.sortBy === field && s.dir === 'asc' ? 'desc' : 'asc' }));

  return (
    <>
      <div className="toolbar">
        <input placeholder="Search name, email, skill, position…" value={q} onChange={(e) => setQ(e.target.value)} />
        <select value={filters.stage} onChange={(e) => { setFilters({ ...filters, stage: e.target.value }); setPage(0); }}>
          <option value="">All stages</option>
          {STAGES.map((s) => <option key={s}>{s}</option>)}
        </select>
        <input type="number" min="0" placeholder="Min yrs exp" style={{ width: 110 }} value={filters.minExperience}
          onChange={(e) => { setFilters({ ...filters, minExperience: e.target.value }); setPage(0); }} />
        <button className="primary" onClick={() => setShowForm(true)}>+ Add candidate</button>
      </div>

      {err && <p className="error">{err}</p>}

      <table>
        <thead>
          <tr>
            <th onClick={() => toggleSort('fullName')}>Name</th>
            <th onClick={() => toggleSort('position')}>Position</th>
            <th onClick={() => toggleSort('experienceYears')}>Exp</th>
            <th onClick={() => toggleSort('stage')}>Stage</th>
            <th onClick={() => toggleSort('createdAt')}>Applied</th>
          </tr>
        </thead>
        <tbody>
          {data.items.map((c) => (
            <tr key={c.id} onClick={() => setSelected(c.id)}>
              <td><b>{c.fullName}</b><div className="muted">{c.email}</div></td>
              <td>{c.position}</td>
              <td>{c.experienceYears ?? '—'}</td>
              <td><span className={`badge ${c.stage}`}>{c.stage}</span></td>
              <td>{new Date(c.createdAt).toLocaleDateString()}</td>
            </tr>
          ))}
          {data.items.length === 0 && <tr><td colSpan="5" className="muted">No candidates found</td></tr>}
        </tbody>
      </table>

      <div className="pager">
        <button disabled={page === 0} onClick={() => setPage(page - 1)}>Prev</button>
        <span>Page {data.totalPages ? page + 1 : 0} / {data.totalPages} · {data.totalItems} total</span>
        <button disabled={page + 1 >= data.totalPages} onClick={() => setPage(page + 1)}>Next</button>
      </div>

      {showForm && <CandidateForm onClose={() => setShowForm(false)} onSaved={() => { setShowForm(false); load(); }} />}
      {selected && <CandidateDetail id={selected} onClose={() => setSelected(null)} onChanged={load} />}
    </>
  );
}
