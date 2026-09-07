import './index.css';
import AppLayout from './components/layout/AppLayout';
import { SurgicalDayProvider, useSurgicalDay } from './contexts/SurgicalDayContext';
import BillPage from './pages/BillPage';
import DayListPage from './pages/DayListPage';
import LiveBoardPage from './pages/LiveBoardPage';

function CurrentPage() {
  const { view } = useSurgicalDay();
  if (view === 'live') return <LiveBoardPage />;
  if (view === 'bill') return <BillPage />;
  return <DayListPage />;
}

export default function App() {
  return <SurgicalDayProvider><AppLayout><CurrentPage /></AppLayout></SurgicalDayProvider>;
}
