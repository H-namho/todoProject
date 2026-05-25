export function getProfile(api) {
  return api.request("/api/user/getinfo");
}

export function updateNickname(api, nickname) {
  return api.request("/api/user/edit", {
    method: "PATCH",
    body: JSON.stringify({ nickname }),
  });
}

export function updatePassword(api, nowPassword, newPassword) {
  return api.request("/api/user/editPw", {
    method: "PATCH",
    body: JSON.stringify({ nowPassword, newPassword }),
  });
}
