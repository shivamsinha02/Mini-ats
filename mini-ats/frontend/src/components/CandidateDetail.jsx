import React, { useEffect, useState, useCallback } from 'react';
import { api } from '../api.js';
import CandidateForm from './CandidateForm.jsx';

export default function CandidateDetail({ id, onClose, onChanged }) {
  const [c, setC] = useState(null);
  const [hist, setHist] = useState([]);
  const [comment, setComment] = useState('');
  const [err, setErr] = useState('');
  const [editing, setEditing] = useState(false);

  const loadCandidate = useCallback(async () => {
    try {
      const res = await fetch(`/api/candidates/${id}`);
      if (!res.ok) throw new Error('Candidate not found');
      setC(await res.json());
      setHist(await api.history(id));
    } catch (e) { setErr(e.message); }
  }, [id]);

  useEffect(() => { loadCandidate(); }, [loadCandidate]);

  const move = async (stage) => {
    try {
      setC(await api.changeStage(id, stage, comment));
      setComment(''); setErr('');
      setHist(await api.history(id));
      onChanged();
    } catch (e) { setErr(e.message); }
  };

  const del = async () => {
    if (!confirm('Delete this candidate?')) return;
    await api.remove(id);
    onChanged(); onClose();
  };

  if (editing && c)
    return <CandidateForm initial={c} onClose={() => setEditing(false)}
      onSaved={() => { setEditing(false); loadCandidate(); onChanged(); }} />;

  return (
    <div className="modal-bg" onClick={onClose}>
      <div className="modal wide" onClick={(e) => e.stopPropagation()}>
        {!c ? <p>{err || 'Loading…'}</p> : (
          <>
            <div className="row-between">
              <div>
                <h3>{c.fullName}</h3>
                <div className="muted">{c.position} · {c.email}{c.phone ? ` · ${c.phone}` : ''}</div>
              </div>
              <span className={`badge ${c.stage}`}>{c.stage}</span>
            </div>
            {c.skills && <p><b>Skills:</b> {c.skills}</p>}
            {c.experienceYears != null && <p><b>Experience:</b> {c.experienceYears} yrs</p>}
            {c.notes && <p className="notes">{c.notes}</p>}

            <h4>Move to next stage</h4>
            {c.allowedNext.length === 0 ? <p className="muted">Final stage reached.</p> : (
              <>
                <input placeholder="Optional comment" value={comment} onChange={(e) => setComment(e.target.value)} />
                <div className="row">
                  {c.allowedNext.map((s) => (
                    <button key={s} className={s === 'REJECTED' ? 'danger' : 'primary'} onClick={() => move(s)}>{s}</button>
                  ))}
                </div>
              </>
            )}
            {err && <p className="error">{err}</p>}

            <h4>History</h4>
            <ul className="timeline">
              {hist.map((h, i) => (
                <li key={i}>
                  <b>{h.fromStage ? `${h.fromStage} → ${h.toStage}` : h.toStage}</b>
                  <span className="muted"> · {new Date(h.changedAt).toLocaleString()}</span>
                  {h.comment && <div>{h.comment}</div>}
                </li>
              ))}
            </ul>

            <div className="row-between">
              <div className="row">
                <button onClick={() => setEditing(true)}>Edit</button>
                <button className="danger" onClick={del}>Delete</button>
              </div>
              <button onClick={onClose}>Close</button>
            </div>
          </>
        )}
      </div>
    </div>
  );
}
