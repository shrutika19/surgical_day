import { useEffect, useState } from 'react';
import { api } from '../services/api';

export function useReferenceData() {
  const [data, setData] = useState({ theatres: [], surgeons: [], procedures: [], patients: [] });
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    let cancelled = false;

    async function load() {
      try {
        const [theatres, surgeons, procedures, patients] = await Promise.all([
          api.getTheatres(), api.getSurgeons(), api.getProcedures(), api.getPatients(),
        ]);
        if (!cancelled) setData({ theatres, surgeons, procedures, patients });
      } catch (requestError) {
        if (!cancelled) setError(requestError.message);
      } finally {
        if (!cancelled) setLoading(false);
      }
    }

    load();
    return () => { cancelled = true; };
  }, []);

  return { ...data, loading, error };
}
