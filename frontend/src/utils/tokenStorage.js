const TOKEN_KEY = "memory-practice.tokens";

export function readTokens() {
  try {
    return JSON.parse(localStorage.getItem(TOKEN_KEY)) ?? {};
  } catch {
    return {};
  }
}

export function saveTokens(tokens) {
  localStorage.setItem(TOKEN_KEY, JSON.stringify(tokens));
}

export function clearTokens() {
  localStorage.removeItem(TOKEN_KEY);
}
