export function formatDate(date: number): string {
  const dateObj = new Date(date);

  return dateObj.toLocaleDateString("de-DE", {
    day: "2-digit",
    month: "long",
    year: "numeric",
  });
}
