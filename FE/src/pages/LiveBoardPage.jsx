import CaseTable from '../components/common/CaseTable';
import { useSurgicalDay } from '../contexts/SurgicalDayContext';

export default function LiveBoardPage() {
  const { date } = useSurgicalDay();
  return <section className="panel"><h2>Live board — {date}</h2><CaseTable allowAdvance /></section>;
}
