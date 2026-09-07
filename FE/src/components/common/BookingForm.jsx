import { useSurgicalDay } from '../../contexts/SurgicalDayContext';

export default function BookingForm() {
  const { form, setForm, patients, procedures, theatres, surgeons, bookCase, busy, loading } = useSurgicalDay();
  const update = (field) => (event) => setForm({ ...form, [field]: event.target.value });
  return (
    <section className="panel">
      <h2>Book a case</h2>
      <form onSubmit={bookCase}>
        <div className="row">
          <div className="field"><label>Patient</label><select value={form.patientId} onChange={update('patientId')} required>{patients.map((patient) => <option key={patient.id} value={patient.id}>{patient.fullName} ({patient.medicalRecordNumber})</option>)}</select></div>
          <div className="field"><label>Procedure</label><select value={form.procedureId} onChange={update('procedureId')} required>{procedures.map((procedure) => <option key={procedure.id} value={procedure.id}>{procedure.name} ({procedure.defaultMinutes} min)</option>)}</select></div>
        </div>
        <div className="row">
          <div className="field"><label>Theatre</label><select value={form.theatreId} onChange={update('theatreId')} required>{theatres.map((theatre) => <option key={theatre.id} value={theatre.id}>{theatre.name} — {theatre.building?.name} [{[...(theatre.equipment || [])].join(', ')}]</option>)}</select></div>
          <div className="field"><label>Surgeon</label><select value={form.surgeonId} onChange={update('surgeonId')} required>{surgeons.map((surgeon) => <option key={surgeon.id} value={surgeon.id}>{surgeon.name} ({surgeon.specialty})</option>)}</select></div>
        </div>
        <div className="row">
          <div className="field"><label>Start time</label><input type="time" value={form.startTime} onChange={update('startTime')} required /></div>
          <div className="field"><label>Duration (minutes, optional)</label><input type="number" min="15" placeholder="Use procedure default" value={form.durationMinutes} onChange={update('durationMinutes')} /></div>
          <button className="btn" type="submit" disabled={busy || loading}>Add to list</button>
        </div>
      </form>
    </section>
  );
}
