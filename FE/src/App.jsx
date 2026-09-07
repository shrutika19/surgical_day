import { useEffect, useState } from 'react';
import { api } from './api';
import './index.css';

function tomorrowIso() {
  const d = new Date();
  d.setDate(d.getDate() + 1);
  return d.toISOString().slice(0, 10);
}

function nextStatus(status) {
  if (status === 'SCHEDULED') return 'IN_THEATRE';
  if (status === 'IN_THEATRE') return 'IN_RECOVERY';
  if (status === 'IN_RECOVERY') return 'DISCHARGED';
  return null;
}

function nextLabel(status) {
  if (status === 'SCHEDULED') return 'Start in theatre';
  if (status === 'IN_THEATRE') return 'Move to recovery';
  if (status === 'IN_RECOVERY') return 'Discharge';
  return null;
}

export default function App() {
  const [view, setView] = useState('day');
  const [date, setDate] = useState(tomorrowIso());
  const [occupancyAt, setOccupancyAt] = useState('14:00');

  const [theatres, setTheatres] = useState([]);
  const [surgeons, setSurgeons] = useState([]);
  const [procedures, setProcedures] = useState([]);
  const [patients, setPatients] = useState([]);
  const [cases, setCases] = useState([]);
  const [occupancy, setOccupancy] = useState(null);

  const [loading, setLoading] = useState(true);
  const [busy, setBusy] = useState(false);
  const [error, setError] = useState('');
  const [info, setInfo] = useState('');

  const [bill, setBill] = useState(null);
  const [selectedCaseId, setSelectedCaseId] = useState(null);

  const [form, setForm] = useState({
    patientId: '',
    procedureId: '',
    theatreId: '',
    surgeonId: '',
    startTime: '08:00',
    durationMinutes: '',
  });

  useEffect(() => {
    let cancelled = false;
    async function loadRefs() {
      setLoading(true);
      setError('');
      try {
        const [t, s, p, pts] = await Promise.all([
          api.getTheatres(),
          api.getSurgeons(),
          api.getProcedures(),
          api.getPatients(),
        ]);
        if (cancelled) return;
        setTheatres(t);
        setSurgeons(s);
        setProcedures(p);
        setPatients(pts);
        setForm((f) => ({
          ...f,
          patientId: pts[0]?.id?.toString() || '',
          procedureId: p[0]?.id?.toString() || '',
          theatreId: t[0]?.id?.toString() || '',
          surgeonId: s[0]?.id?.toString() || '',
        }));
      } catch (e) {
        if (!cancelled) setError(e.message);
      } finally {
        if (!cancelled) setLoading(false);
      }
    }
    loadRefs();
    return () => {
      cancelled = true;
    };
  }, []);

  async function refreshDay() {
    setError('');
    try {
      const [caseList, occ] = await Promise.all([
        api.getCases(date),
        api.getOccupancy(date, occupancyAt.length === 5 ? `${occupancyAt}:00` : occupancyAt),
      ]);
      setCases(caseList);
      setOccupancy(occ);
    } catch (e) {
      setError(e.message);
    }
  }

  useEffect(() => {
    if (!loading) {
      refreshDay();
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [date, occupancyAt, loading]);

  async function onBook(e) {
    e.preventDefault();
    setBusy(true);
    setError('');
    setInfo('');
    try {
      const payload = {
        surgeryDate: date,
        startTime: form.startTime.length === 5 ? `${form.startTime}:00` : form.startTime,
        patientId: Number(form.patientId),
        procedureId: Number(form.procedureId),
        theatreId: Number(form.theatreId),
        surgeonId: Number(form.surgeonId),
      };
      if (form.durationMinutes) {
        payload.durationMinutes = Number(form.durationMinutes);
      }
      await api.bookCase(payload);
      setInfo('Case booked on the day list.');
      await refreshDay();
    } catch (err) {
      setError(err.message);
    } finally {
      setBusy(false);
    }
  }

  async function advance(caseItem) {
    const status = nextStatus(caseItem.status);
    if (!status) return;
    setBusy(true);
    setError('');
    setInfo('');
    try {
      const updated = await api.updateStatus(caseItem.id, status);
      if (status === 'DISCHARGED') {
        const b = await api.getBill(caseItem.id);
        setBill(b);
        setSelectedCaseId(caseItem.id);
        setView('bill');
        setInfo(`Discharged ${updated.patientName}. Bill ready.`);
      } else {
        setInfo(`${updated.patientName} → ${status}`);
      }
      await refreshDay();
    } catch (err) {
      setError(err.message);
    } finally {
      setBusy(false);
    }
  }

  async function openBill(caseId) {
    setBusy(true);
    setError('');
    try {
      const b = await api.getBill(caseId);
      setBill(b);
      setSelectedCaseId(caseId);
      setView('bill');
    } catch (err) {
      setError(err.message);
    } finally {
      setBusy(false);
    }
  }

  return (
    <div className="app-shell">
      <header className="brand-bar">
        <div>
          <h1>Surgical Day</h1>
          <p>
            Plan tomorrow&apos;s list across four theatres, catch surgeon and equipment clashes,
            watch recovery beds, and hand the patient a bill at discharge.
          </p>
        </div>
      </header>

      <nav className="nav">
        <button className={view === 'day' ? 'active' : ''} onClick={() => setView('day')}>
          Day list
        </button>
        <button className={view === 'live' ? 'active' : ''} onClick={() => setView('live')}>
          Live board
        </button>
        <button className={view === 'bill' ? 'active' : ''} onClick={() => setView('bill')}>
          Discharge bill
        </button>
      </nav>

      <div className="panel">
        <div className="row">
          <div className="field">
            <label htmlFor="date">Surgery date</label>
            <input
              id="date"
              type="date"
              value={date}
              onChange={(e) => setDate(e.target.value)}
            />
          </div>
          <div className="field">
            <label htmlFor="occAt">Recovery check time</label>
            <input
              id="occAt"
              type="time"
              value={occupancyAt}
              onChange={(e) => setOccupancyAt(e.target.value)}
            />
          </div>
          <button className="btn secondary" type="button" onClick={refreshDay} disabled={busy}>
            Refresh
          </button>
        </div>
        {occupancy && (
          <p className="muted">
            Recovery at {occupancyAt}: {occupancy.occupied}/{occupancy.bedCapacity} beds
            {occupancy.full ? ' — FULL' : ''}
          </p>
        )}
      </div>

      {error && <div className="error">{error}</div>}
      {info && <div className="success">{info}</div>}
      {loading && <p className="muted">Loading reference data…</p>}

      {view === 'day' && (
        <>
          <section className="panel">
            <h2>Book a case</h2>
            <form onSubmit={onBook}>
              <div className="row">
                <div className="field">
                  <label>Patient</label>
                  <select
                    value={form.patientId}
                    onChange={(e) => setForm({ ...form, patientId: e.target.value })}
                    required
                  >
                    {patients.map((p) => (
                      <option key={p.id} value={p.id}>
                        {p.fullName} ({p.medicalRecordNumber})
                      </option>
                    ))}
                  </select>
                </div>
                <div className="field">
                  <label>Procedure</label>
                  <select
                    value={form.procedureId}
                    onChange={(e) => setForm({ ...form, procedureId: e.target.value })}
                    required
                  >
                    {procedures.map((p) => (
                      <option key={p.id} value={p.id}>
                        {p.name} ({p.defaultMinutes} min)
                      </option>
                    ))}
                  </select>
                </div>
              </div>
              <div className="row">
                <div className="field">
                  <label>Theatre</label>
                  <select
                    value={form.theatreId}
                    onChange={(e) => setForm({ ...form, theatreId: e.target.value })}
                    required
                  >
                    {theatres.map((t) => (
                      <option key={t.id} value={t.id}>
                        {t.name} — {t.building?.name} [{[...(t.equipment || [])].join(', ')}]
                      </option>
                    ))}
                  </select>
                </div>
                <div className="field">
                  <label>Surgeon</label>
                  <select
                    value={form.surgeonId}
                    onChange={(e) => setForm({ ...form, surgeonId: e.target.value })}
                    required
                  >
                    {surgeons.map((s) => (
                      <option key={s.id} value={s.id}>
                        {s.name} ({s.specialty})
                      </option>
                    ))}
                  </select>
                </div>
              </div>
              <div className="row">
                <div className="field">
                  <label>Start time</label>
                  <input
                    type="time"
                    value={form.startTime}
                    onChange={(e) => setForm({ ...form, startTime: e.target.value })}
                    required
                  />
                </div>
                <div className="field">
                  <label>Duration (minutes, optional)</label>
                  <input
                    type="number"
                    min="15"
                    placeholder="Use procedure default"
                    value={form.durationMinutes}
                    onChange={(e) => setForm({ ...form, durationMinutes: e.target.value })}
                  />
                </div>
                <button className="btn" type="submit" disabled={busy || loading}>
                  Add to list
                </button>
              </div>
            </form>
          </section>

          <section className="panel">
            <h2>Tomorrow&apos;s list — {date}</h2>
            <CaseTable cases={cases} onAdvance={null} onBill={openBill} />
          </section>
        </>
      )}

      {view === 'live' && (
        <section className="panel">
          <h2>Live board — {date}</h2>
          <CaseTable cases={cases} onAdvance={advance} onBill={openBill} busy={busy} />
        </section>
      )}

      {view === 'bill' && (
        <section className="panel">
          <h2>Discharge bill</h2>
          {!bill && (
            <p className="muted">
              Discharge a patient from the live board, or open a bill for a discharged case.
              {selectedCaseId ? ` Last case id: ${selectedCaseId}` : ''}
            </p>
          )}
          {bill && (
            <div>
              <p>
                Patient: <strong>{bill.patientName}</strong>
              </p>
              <p className="muted">Case #{bill.surgicalCaseId}</p>
              <div className="table-wrap">
                <table>
                  <thead>
                    <tr>
                      <th>Description</th>
                      <th>Amount</th>
                    </tr>
                  </thead>
                  <tbody>
                    {bill.lines.map((line, idx) => (
                      <tr key={idx}>
                        <td>{line.description}</td>
                        <td>{Number(line.amount).toFixed(2)}</td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
              <div className="bill-total">Total: {Number(bill.totalAmount).toFixed(2)}</div>
            </div>
          )}
        </section>
      )}
    </div>
  );
}

function CaseTable({ cases, onAdvance, onBill, busy }) {
  if (!cases.length) {
    return <p className="muted">No cases booked for this date.</p>;
  }

  return (
    <div className="table-wrap">
      <table>
        <thead>
          <tr>
            <th>Time</th>
            <th>Patient</th>
            <th>Procedure</th>
            <th>Theatre</th>
            <th>Surgeon</th>
            <th>Status</th>
            <th>Actions</th>
          </tr>
        </thead>
        <tbody>
          {cases.map((c) => (
            <tr key={c.id}>
              <td>
                {c.startTime?.slice(0, 5)} ({c.durationMinutes}m)
              </td>
              <td>{c.patientName}</td>
              <td>{c.procedureName}</td>
              <td>
                {c.theatreName}
                <div className="muted">{c.buildingName}</div>
              </td>
              <td>{c.surgeonName}</td>
              <td>
                <span className={`badge ${c.status}`}>{c.status}</span>
              </td>
              <td>
                <div className="actions">
                  {onAdvance && nextStatus(c.status) && (
                    <button
                      className="btn"
                      type="button"
                      disabled={busy}
                      onClick={() => onAdvance(c)}
                    >
                      {nextLabel(c.status)}
                    </button>
                  )}
                  {c.status === 'DISCHARGED' && (
                    <button
                      className="btn secondary"
                      type="button"
                      disabled={busy}
                      onClick={() => onBill(c.id)}
                    >
                      View bill
                    </button>
                  )}
                </div>
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}
