export function today() {
  return new Date().toISOString().slice(0, 10);
}

export function addDays(days) {
  const date = new Date();
  date.setDate(date.getDate() + days);
  return date.toISOString().slice(0, 10);
}

export function emptyTodoForm() {
  return { title: "", memo: "", dueDate: today(), todoPriority: "MEDIUM" };
}

export function emptyRepeatForm() {
  return { title: "", memo: "", repeatType: "DAILY", dayOfWeek: [], startDate: today(), endDate: "" };
}
