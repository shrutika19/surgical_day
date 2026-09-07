import { useCallback, useEffect, useState } from 'react';
import { api } from '../services/api';

export function useDayData(date, occupancyAt, enabled = true) {
  const [cases, setCases] = useState([]);
  const [occupancy, setOccupancy] = useState(null);
  const [error, setError] = useState('');

  const refreshDay = useCallback(async () => {
    setError('');
    try {
      const [caseList, occupancyData] = await Promise.all([
        api.getCases(date),
        api.getOccupancy(date, occupancyAt.length === 5 ? `${occupancyAt}:00` : occupancyAt),
      ]);
      setCases(caseList);
      setOccupancy(occupancyData);
    } catch (requestError) {
      setError(requestError.message);
    }
  }, [date, occupancyAt]);

  useEffect(() => {
    if (enabled) refreshDay();
  }, [enabled, refreshDay]);

  return { cases, setCases, occupancy, refreshDay, error, setError };
}
