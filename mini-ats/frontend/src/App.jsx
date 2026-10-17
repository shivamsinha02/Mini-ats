
import React, { useEffect, useState } from 'react';
import Dashboard from './components/Dashboard.jsx';
import Candidates from './components/Candidates.jsx';

export default function App() {
  const [tab, setTab] = useState('dashboard');

  const [darkMode, setDarkMode] = useState(
    () => localStorage.getItem('mini-ats-theme') === 'dark'
  );

  useEffect(() => {
    localStorage.setItem('mini-ats-theme', darkMode ? 'dark' : 'light');
  }, [darkMode]);

  return (
    <div className={`app ${darkMode ? 'dark-mode' : ''}`}>
      <header>
        <h1>Mini ATS</h1>

        <div className="header-actions">
          <nav>
            {['dashboard', 'candidates'].map((t) => (
              <button
                key={t}
                className={tab === t ? 'tab active' : 'tab'}
                onClick={() => setTab(t)}
              >
                {t[0].toUpperCase() + t.slice(1)}
              </button>
            ))}
          </nav>

          <button
            type="button"
            className="theme-toggle"
            onClick={() => setDarkMode((current) => !current)}
            aria-label={darkMode ? 'Switch to light mode' : 'Switch to dark mode'}
            title={darkMode ? 'Switch to light mode' : 'Switch to dark mode'}
          >
            <span aria-hidden="true">{darkMode ? '☀️' : '🌙'}</span>
            <span className="theme-label">
              {darkMode ? 'Light mode' : 'Dark mode'}
            </span>
          </button>
        </div>
      </header>

      <main>
        {tab === 'dashboard' ? <Dashboard /> : <Candidates />}
      </main>

      <footer className="app-footer">
        <span>
          Made with <span className="footer-heart">♥</span> by Shivam
        </span>

        <a
          href="https://github.com/shivamsinha02"
          target="_blank"
          rel="noopener noreferrer"
          className="github-link"
          aria-label="Visit Shivam's GitHub profile"
          title="GitHub profile"
        >
          <svg
            viewBox="0 0 24 24"
            width="21"
            height="21"
            fill="currentColor"
            aria-hidden="true"
          >
            <path d="M12 .9a11.1 11.1 0 0 0-3.51 21.63c.55.1.76-.24.76-.53v-2.02c-3.1.67-3.76-1.32-3.76-1.32-.5-1.28-1.24-1.62-1.24-1.62-1.01-.69.08-.68.08-.68 1.12.08 1.71 1.15 1.71 1.15.99 1.7 2.59 1.21 3.22.93.1-.72.39-1.21.7-1.49-2.47-.28-5.07-1.24-5.07-5.5 0-1.22.44-2.22 1.15-3-.12-.28-.5-1.42.11-2.96 0 0 .94-.3 3.05 1.15a10.6 10.6 0 0 1 5.55 0c2.11-1.45 3.05-1.15 3.05-1.15.61 1.54.23 2.68.11 2.96.72.78 1.15 1.78 1.15 3 0 4.27-2.6 5.21-5.08 5.49.4.35.75 1.02.75 2.06V22c0 .29.2.63.76.53A11.1 11.1 0 0 0 12 .9Z" />
          </svg>
        </a>
      </footer>
    </div>
  );
}
