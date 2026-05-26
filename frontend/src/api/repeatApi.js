export function getRepeats(api) {
  return api.request("/api/repeat/list");
}

export function createRepeat(api, form) {
  return api.request("/api/repeat/write", {
    method: "POST",
    body: JSON.stringify(form),
  });
}

export function toggleRepeatActive(api, repeatId) {
  return api.request(`/api/repeat/${repeatId}/active`, { method: "PATCH" });
}

export function completeRepeatToday(api, repeatId) {
  return api.request(`/api/repeat/${repeatId}/completed`, { method: "PATCH" });
}

export function cancelRepeatToday(api, repeatId) {
  return api.request(`/api/repeat/${repeatId}/completed`, { method: "DELETE" });
}

export function deleteRepeat(api, repeatId) {
  return api.request(`/api/repeat/${repeatId}`, { method: "DELETE" });
}

export function updateRepeat(api, repeatId, form) {
  return api.request(`/api/repeat/${repeatId}`, {
    method: "PATCH",
    body: JSON.stringify(form),
  });
}

export function getRepeatCompletionDays(api, repeatId) {
  return api.request(`/api/repeat/${repeatId}/day`);
}

export function getRepeatMonthlyCount(api, repeatId, monthValue) {
  const [year, month] = monthValue.split("-");
  const params = new URLSearchParams({ year, month });
  return api.request(`/api/repeat/${repeatId}/allday?${params.toString()}`);
}
