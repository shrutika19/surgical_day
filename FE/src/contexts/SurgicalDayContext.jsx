import { createContext, useContext, useEffect, useState } from 'react';
import { useDayData } from '../hooks/useDayData';
import { useReferenceData } from '../hooks/useReferenceData';
import { api } from '../services/api';
import { apiTime, tomorrowIso } from '../utils/date';
import { nextStatus } from '../utils/caseStatus';

const SurgicalDayContext = createContext(null);

export function SurgicalDayProvider({ children }) {
  const [view, setView] = useState('day');
  const [date, setDate] = useState(tomorrowIso());
  const [occupancyAt, setOccupancyAt] = useState('14:00');
  const [busy, setBusy] = useState(false);
  const [info, setInfo] = useState('');
  const [bill, setBill] = useState(null);
  const [selectedCaseId, setSelectedCaseId] = useState(null);
  const [form, setForm] = useState({
    patientId: '', procedureId: '', theatreId: '', surgeonId: '', startTime: '08:00', durationMinutes: '',
  });

  const references = useReferenceData();
  const day = useDayData(date, occupancyAt, !references.loading);
  const error = references.error || day.error;

  function initializeForm(fieldData) {
    setForm((current) => ({
      ...current,
      patientId: current.patientId || fieldData.patients[0]?.id?.toString() || '',
      procedureId: current.procedureId || fieldData.procedures[0]?.id?.toString() || '',
      theatreId: current.theatreId || fieldData.theatres[0]?.id?.toString() || '',
      surgeonId: current.surgeonId || fieldData.surgeons[0]?.id?.toString() || '',
    }));
  }

  useEffect(() => {
    if (!references.loading && references.patients.length && !form.patientId) initializeForm(references);
  }, [references.loading, references.patients, references.procedures, references.theatres, references.surgeons, form.patientId]);

  async function bookCase(event) {
    event.preventDefault();
    setBusy(true); setInfo(''); day.setError('');
    try {
      const payload = {
        surgeryDate: date, startTime: apiTime(form.startTime),
        patientId: Number(form.patientId), procedureId: Number(form.procedureId),
        theatreId: Number(form.theatreId), surgeonId: Number(form.surgeonId),
      };
      if (form.durationMinutes) payload.durationMinutes = Number(form.durationMinutes);
      await api.bookCase(payload);
      setInfo('Case booked on the day list.');
      await day.refreshDay();
    } catch (requestError) { day.setError(requestError.message); }
    finally { setBusy(false); }
  }

  async function advanceCase(caseItem) {
    const status = nextStatus(caseItem.status);
    if (!status) return;
    setBusy(true); setInfo(''); day.setError('');
    try {
      const updated = await api.updateStatus(caseItem.id, status);
      if (status === 'DISCHARGED') {
        setBill(await api.getBill(caseItem.id)); setSelectedCaseId(caseItem.id); setView('bill');
        setInfo(`Discharged ${updated.patientName}. Bill ready.`);
      } else setInfo(`${updated.patientName} -> ${status}`);
      await day.refreshDay();
    } catch (requestError) { day.setError(requestError.message); }
    finally { setBusy(false); }
  }

  async function openBill(caseId) {
    setBusy(true); day.setError('');
    try { setBill(await api.getBill(caseId)); setSelectedCaseId(caseId); setView('bill'); }
    catch (requestError) { day.setError(requestError.message); }
    finally { setBusy(false); }
  }

  return (
    <SurgicalDayContext.Provider value={{
      view, setView, date, setDate, occupancyAt, setOccupancyAt, busy, info, error,
      bill, selectedCaseId, form, setForm, ...references, ...day,
      initializeForm, bookCase, advanceCase, openBill,
    }}>
      {children}
    </SurgicalDayContext.Provider>
  );
}

export function useSurgicalDay() {
  return useContext(SurgicalDayContext);
}
