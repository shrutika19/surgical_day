import { useSurgicalDay } from '../../contexts/SurgicalDayContext';

export default function BillDetails() {
  const { bill, selectedCaseId } = useSurgicalDay();
  if (!bill) return <p className="muted">Discharge a patient from the live board, or open a bill for a discharged case.{selectedCaseId ? ` Last case id: ${selectedCaseId}` : ''}</p>;
  return <div><p>Patient: <strong>{bill.patientName}</strong></p><p className="muted">Case #{bill.surgicalCaseId}</p><div className="table-wrap"><table><thead><tr><th>Description</th><th>Amount</th></tr></thead><tbody>{bill.lines.map((line, index) => <tr key={index}><td>{line.description}</td><td>{Number(line.amount).toFixed(2)}</td></tr>)}</tbody></table></div><div className="bill-total">Total: {Number(bill.totalAmount).toFixed(2)}</div></div>;
}
