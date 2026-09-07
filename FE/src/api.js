const API_BASE = import.meta.env.VITE_API_URL || '';

async function request(path, options = {}) {
  const response = await fetch(`${API_BASE}${path}`, {
    headers: {
      'Content-Type': 'application/json',
      ...(options.headers || {}),
    },
    ...options,
  });

  const text = await response.text();
  let body = null;
  if (text) {
    try {
      body = JSON.parse(text);
    } catch {
      body = { message: text };
    }
  }

  if (!response.ok) {
    const message = body?.message || `Request failed (${response.status})`;
    throw new Error(message);
  }

  return body;
}

export const api = {
  getBuildings: () => request('/api/buildings'),
  getTheatres: () => request('/api/theatres'),
  getSurgeons: () => request('/api/surgeons'),
  getProcedures: () => request('/api/procedures'),
  getPatients: () => request('/api/patients'),
  getRecoveryBeds: () => request('/api/recovery-beds'),
  getCases: (date) => request(`/api/cases?date=${date}`),
  bookCase: (payload) =>
    request('/api/cases', { method: 'POST', body: JSON.stringify(payload) }),
  updateStatus: (id, status) =>
    request(`/api/cases/${id}/status`, {
      method: 'PATCH',
      body: JSON.stringify({ status }),
    }),
  getBill: (id) => request(`/api/cases/${id}/bill`),
  getOccupancy: (date, at) =>
    request(`/api/recovery/occupancy?date=${date}&at=${at}`),
};
