import { useEffect, useState } from "react";
import { openNoticeStream } from "../api/noticeStream";

export default function useNotices(accessToken, notify, onTodoNotice) {
  const [notices, setNotices] = useState([]);

  useEffect(() => {
    if (!accessToken) {
      setNotices([]);
      return undefined;
    }

    return openNoticeStream({
      accessToken,
      onEvent: ({ event, data }) => {
        if (event !== "today-todo") return;
        const titles = Array.isArray(data) ? data : [String(data)];
        const receivedAt = new Date().toLocaleTimeString("ko-KR", { hour: "2-digit", minute: "2-digit" });
        setNotices((items) => [{ id: `${Date.now()}-${Math.random()}`, receivedAt, titles }, ...items].slice(0, 5));
        notify(`오늘 할 일 ${titles.length}개가 도착했습니다.`, "notice");
        onTodoNotice();
      },
      onError: () => notify("알림 연결을 다시 시도하고 있습니다.", "error"),
    });
  }, [accessToken, notify, onTodoNotice]);

  return notices;
}
