import { Bell } from "lucide-react";

export default function NoticeStrip({ notices }) {
  if (notices.length === 0) return null;

  return (
    <section className="notice-strip" aria-live="polite">
      <div className="notice-title">
        <Bell size={18} />
        <strong>실시간 알림</strong>
      </div>
      <div className="notice-list">
        {notices.map((notice) => (
          <div className="notice-item" key={notice.id}>
            <span>{notice.receivedAt}</span>
            <p>{notice.titles.join(", ")}</p>
          </div>
        ))}
      </div>
    </section>
  );
}
