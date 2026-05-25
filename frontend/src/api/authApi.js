import { parseResponse } from "./client";

export function signUp(form) {
  return fetch("/api/user/signup", {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(form),
  }).then(parseResponse);
}

export function signIn({ username, password }) {
  return fetch("/api/user/signin", {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ username, password }),
  }).then(parseResponse);
}

export function logout(api) {
  return api.request("/api/user/logout", { method: "POST" });
}
