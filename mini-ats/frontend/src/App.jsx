import React, { useState } from 'react';
import Dashboard from './components/Dashboard.jsx';
import Candidates from './components/Candidates.jsx';

export default function App() {
  const [tab, setTab] = useState('dashboard');
  return (
    <div className="app">
      <header>
        <h1>Mini ATS</h1>
        <nav>
          {['dashboard', 'candidates'].map((t) => (
            <button key={t} className={tab === t ? 'tab active' : 'tab'} onClick={() => setTab(t)}>
              {t[0].toUpperCase() + t.slice(1)}
            </button>
          ))}
        </nav>
      </header>
      <main>{tab === 'dashboard' ? <Dashboard /> : <Candidates />}</main>
    </div>
  );
}
