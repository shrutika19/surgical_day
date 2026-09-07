import { useSurgicalDay } from '../../contexts/SurgicalDayContext';

export default function Navigation() {
  const { view, setView } = useSurgicalDay();
  return <nav className="nav"><button className={view === 'day' ? 'active' : ''} onClick={() => setView('day')}>Day list</button><button className={view === 'live' ? 'active' : ''} onClick={() => setView('live')}>Live board</button><button className={view === 'bill' ? 'active' : ''} onClick={() => setView('bill')}>Discharge bill</button></nav>;
}
