export function tomorrowIso() {
  const date = new Date();
  date.setDate(date.getDate() + 1);
  return date.toISOString().slice(0, 10);
}

export function apiTime(time) {
  return time.length === 5 ? `${time}:00` : time;
}
