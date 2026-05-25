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
