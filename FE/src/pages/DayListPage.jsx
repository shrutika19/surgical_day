import BookingForm from '../components/common/BookingForm';
import CaseTable from '../components/common/CaseTable';
import { useSurgicalDay } from '../contexts/SurgicalDayContext';

export default function DayListPage() {
  const { date } = useSurgicalDay();
  return <><BookingForm /><section className="panel"><h2>Tomorrow&apos;s list — {date}</h2><CaseTable /></section></>;
}
