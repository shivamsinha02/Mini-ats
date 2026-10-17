async function req(path, opts = {}) {
  const res = await fetch('/api' + path, {
    headers: { 'Content-Type': 'application/json' },
    ...opts,
  });
  if (!res.ok) {
    const e = await res.json().catch(() => ({ message: res.statusText }));
    const fields = e.errors ? ' ' + Object.values(e.errors).join(', ') : '';
    throw new Error((e.message || 'Request failed') + fields);
  }
  return res.status === 204 ? null : res.json();
}

const qs = (o) =>
  new URLSearchParams(Object.entries(o).filter(([, v]) => v !== '' && v != null)).toString();

export const api = {
  list: (params) => req('/candidates?' + qs(params)),
  create: (body) => req('/candidates', { method: 'POST', body: JSON.stringify(body) }),
  update: (id, body) => req(`/candidates/${id}`, { method: 'PUT', body: JSON.stringify(body) }),
  remove: (id) => req(`/candidates/${id}`, { method: 'DELETE' }),
  changeStage: (id, stage, comment) =>
    req(`/candidates/${id}/stage`, { method: 'PATCH', body: JSON.stringify({ stage, comment }) }),
  history: (id) => req(`/candidates/${id}/history`),
  metrics: () => req('/dashboard/metrics'),
};

export const STAGES = ['APPLIED', 'SCREENING', 'INTERVIEW', 'OFFER', 'HIRED', 'REJECTED'];
