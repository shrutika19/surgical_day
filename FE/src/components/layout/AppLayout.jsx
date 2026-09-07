import { useSurgicalDay } from '../../contexts/SurgicalDayContext';
import Header from './Header';
import Navigation from './Navigation';
import DayControls from '../common/DayControls';
import { Alert } from '../common/Alert';

export default function AppLayout({ children }) {
  const { error, info, loading } = useSurgicalDay();
  return <div className="app-shell"><Header /><Navigation /><DayControls /><Alert type="error">{error}</Alert><Alert type="success">{info}</Alert>{loading && <p className="muted">Loading reference data...</p>}{children}</div>;
}
