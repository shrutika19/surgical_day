import { useSurgicalDay } from '../../contexts/SurgicalDayContext';
import { nextLabel, nextStatus } from '../../utils/caseStatus';

export default function CaseTable({ allowAdvance = false }) {
  const { cases, advanceCase, openBill, busy } = useSurgicalDay();
  if (!cases.length) return <p className="muted">No cases booked for this date.</p>;
  return (
    <div className="table-wrap"><table><thead><tr><th>Time</th><th>Patient</th><th>Procedure</th><th>Theatre</th><th>Surgeon</th><th>Status</th><th>Actions</th></tr></thead><tbody>
      {cases.map((item) => <tr key={item.id}>
        <td>{item.startTime?.slice(0, 5)} ({item.durationMinutes}m)</td><td>{item.patientName}</td><td>{item.procedureName}</td>
        <td>{item.theatreName}<div className="muted">{item.buildingName}</div></td><td>{item.surgeonName}</td>
        <td><span className={`badge ${item.status}`}>{item.status}</span></td>
        <td><div className="actions">
          {allowAdvance && nextStatus(item.status) && <button className="btn" type="button" disabled={busy} onClick={() => advanceCase(item)}>{nextLabel(item.status)}</button>}
          {item.status === 'DISCHARGED' && <button className="btn secondary" type="button" disabled={busy} onClick={() => openBill(item.id)}>View bill</button>}
        </div></td>
      </tr>)}
    </tbody></table></div>
  );
}
