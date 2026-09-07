export function nextStatus(status) {
  if (status === 'SCHEDULED') return 'IN_THEATRE';
  if (status === 'IN_THEATRE') return 'IN_RECOVERY';
  if (status === 'IN_RECOVERY') return 'DISCHARGED';
  return null;
}

export function nextLabel(status) {
  if (status === 'SCHEDULED') return 'Start in theatre';
  if (status === 'IN_THEATRE') return 'Move to recovery';
  if (status === 'IN_RECOVERY') return 'Discharge';
  return null;
}
