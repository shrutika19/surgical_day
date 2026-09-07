export function Alert({ type, children }) {
  if (!children) return null;
  return <div className={type}>{children}</div>;
}
