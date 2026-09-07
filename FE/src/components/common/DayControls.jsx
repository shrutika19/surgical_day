import { useSurgicalDay } from '../../contexts/SurgicalDayContext';

export default function DayControls() {
  const { date, setDate, occupancyAt, setOccupancyAt, occupancy, busy, refreshDay } = useSurgicalDay();
  return (
    <section className="panel">
      <div className="row">
        <div className="field"><label htmlFor="date">Surgery date</label><input id="date" type="date" value={date} onChange={(event) => setDate(event.target.value)} /></div>
        <div className="field"><label htmlFor="occAt">Recovery check time</label><input id="occAt" type="time" value={occupancyAt} onChange={(event) => setOccupancyAt(event.target.value)} /></div>
        <button className="btn secondary" type="button" onClick={refreshDay} disabled={busy}>Refresh</button>
      </div>
      {occupancy && <p className="muted">Recovery at {occupancyAt}: {occupancy.occupied}/{occupancy.bedCapacity} beds{occupancy.full ? ' — FULL' : ''}</p>}
    </section>
  );
}
