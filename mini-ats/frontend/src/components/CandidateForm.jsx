import React, { useState } from 'react';
import { api } from '../api.js';

const empty = { fullName: '', email: '', phone: '', position: '', skills: '', experienceYears: '', notes: '' };

export default function CandidateForm({ initial, onClose, onSaved }) {
  const [f, setF] = useState(initial ? { ...empty, ...initial } : empty);
  const [err, setErr] = useState('');
  const set = (k) => (e) => setF({ ...f, [k]: e.target.value });

  const submit = async (e) => {
    e.preventDefault();
    const body = { ...f, experienceYears: f.experienceYears === '' ? null : Number(f.experienceYears) };
    try {
      initial ? await api.update(initial.id, body) : await api.create(body);
      onSaved();
    } catch (x) { setErr(x.message); }
  };

  return (
    <div className="modal-bg" onClick={onClose}>
      <form className="modal" onClick={(e) => e.stopPropagation()} onSubmit={submit}>
        <h3>{initial ? 'Edit candidate' : 'Add candidate'}</h3>
        <label>Full name *<input required value={f.fullName} onChange={set('fullName')} /></label>
        <label>Email *<input required type="email" value={f.email} onChange={set('email')} /></label>
        <label>Phone<input value={f.phone || ''} onChange={set('phone')} /></label>
        <label>Position *<input required value={f.position} onChange={set('position')} /></label>
        <label>Skills (comma separated)<input value={f.skills || ''} onChange={set('skills')} /></label>
        <label>Experience (years)<input type="number" min="0" max="60" value={f.experienceYears ?? ''} onChange={set('experienceYears')} /></label>
        <label>Notes<textarea rows="3" value={f.notes || ''} onChange={set('notes')} /></label>
        {err && <p className="error">{err}</p>}
        <div className="row-end">
          <button type="button" onClick={onClose}>Cancel</button>
          <button className="primary" type="submit">Save</button>
        </div>
      </form>
    </div>
  );
}
