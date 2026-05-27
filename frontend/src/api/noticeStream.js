export function openNoticeStream({ api, onEvent, onError }) {
  const controller = new AbortController();
  const decoder = new TextDecoder();
  let closed = false;
  let retryTimer = null;

  async function connect() {
    try {
      const response = await api.fetchAuthorized("/api/notice/subscribe", {
        headers: {
          Accept: "text/event-stream",
        },
        signal: controller.signal,
      });

      if (!response.ok || !response.body) {
        throw new Error(`notice stream failed (${response.status})`);
      }

      const reader = response.body.getReader();
      let buffer = "";

      while (!closed) {
        const { value, done } = await reader.read();
        if (done) break;
        buffer += decoder.decode(value, { stream: true });
        const chunks = buffer.split("\n\n");
        buffer = chunks.pop() ?? "";
        chunks.forEach((rawEvent) => {
          const event = parseSseEvent(rawEvent);
          if (event) onEvent(event);
        });
      }
    } catch (error) {
      if (!closed && error.name !== "AbortError") onError?.(error);
    }

    if (!closed) retryTimer = window.setTimeout(connect, 3000);
  }

  connect();

  return () => {
    closed = true;
    window.clearTimeout(retryTimer);
    controller.abort();
  };
}

function parseSseEvent(rawEvent) {
  const lines = rawEvent.split("\n");
  let eventName = "message";
  const dataLines = [];

  lines.forEach((line) => {
    if (line.startsWith("event:")) eventName = line.slice(6).trim();
    if (line.startsWith("data:")) dataLines.push(line.slice(5).trim());
  });

  if (dataLines.length === 0) return null;

  const rawData = dataLines.join("\n");
  let data = rawData;
  try {
    data = JSON.parse(rawData);
  } catch {
    // SSE data may be plain text.
  }
  return { event: eventName, data };
}
