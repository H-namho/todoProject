export async function parseResponse(response) {
  const text = await response.text();
  let data = null;

  if (text) {
    try {
      data = JSON.parse(text);
    } catch {
      data = text;
    }
  }

  if (!response.ok) {
    const message = data?.message || data?.error || data || `요청 실패 (${response.status})`;
    throw new Error(message);
  }

  return data;
}

export function createApiClient({ tokens, onTokensChanged, onUnauthorized }) {
  async function fetchAuthorized(path, options = {}) {
    const response = await fetch(path, {
      ...options,
      headers: buildHeaders(options, tokens.accessToken),
    });

    if (response.status !== 401 || !tokens.refreshToken) {
      return response;
    }

    const refreshed = await fetch("/api/user/refresh", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ refreshToken: tokens.refreshToken }),
    });

    if (!refreshed.ok) {
      onUnauthorized();
      return response;
    }

    const nextTokens = await refreshed.json();
    onTokensChanged(nextTokens);
    return requestWithToken(path, options, nextTokens.accessToken);
  }

  async function request(path, options = {}) {
    return parseResponse(await fetchAuthorized(path, options));
  }

  function requestWithToken(path, options, accessToken) {
    return fetch(path, {
      ...options,
      headers: buildHeaders(options, accessToken),
    });
  }

  return { request, fetchAuthorized };
}

function buildHeaders(options, accessToken) {
  return {
    ...(options.body ? { "Content-Type": "application/json" } : {}),
    ...(accessToken ? { Authorization: `Bearer ${accessToken}` } : {}),
    ...options.headers,
  };
}
