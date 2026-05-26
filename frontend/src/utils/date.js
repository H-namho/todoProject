export function today() {
  return formatLocalDate(new Date());
}

export function addDays(days) {
  const date = new Date();
  date.setDate(date.getDate() + days);
  return formatLocalDate(date);
}

export function emptyTodoForm() {
  return { title: "", memo: "", dueDate: today(), todoPriority: "MEDIUM" };
}

export function emptyRepeatForm() {
  return { title: "", memo: "", repeatType: "DAILY", dayOfWeek: [], startDate: today(), endDate: "" };
}

export function currentMonth() {
  return today().slice(0, 7);
}

export function isRepeatDueToday(item) {
  const dateValue = today();

  if (!item.active || dateValue < item.startDate) {
    return false;
  }

  if (item.endDate && dateValue > item.endDate) {
    return false;
  }

  if (item.repeatType === "DAILY") {
    return true;
  }

  const weekDays = ["SUNDAY", "MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY", "SATURDAY"];
  const dayName = weekDays[new Date(`${dateValue}T00:00:00`).getDay()];
  return Array.isArray(item.dayOfWeek) && item.dayOfWeek.includes(dayName);
}

function formatLocalDate(date) {
  const year = date.getFullYear();
  const month = String(date.getMonth() + 1).padStart(2, "0");
  const day = String(date.getDate()).padStart(2, "0");
  return `${year}-${month}-${day}`;
}
