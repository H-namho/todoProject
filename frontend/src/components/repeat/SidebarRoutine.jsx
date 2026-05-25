import { Check, RefreshCw, X } from "lucide-react";
import { DAYS } from "../../constants/options";

export default function SidebarRoutine({ items, onRefresh, onToggle, onComplete }) {
  const activeItems = items.filter((item) => item.active).slice(0, 5);

  return (
    <section className="sidebar-routine">
      <div className="sidebar-section-head">
        <div>
          <span>Routine</span>
          <strong>반복해서 할 일</strong>
        </div>
        <button type="button" onClick={onRefresh} aria-label="반복 목록 새로고침" title="반복 목록 새로고침">
          <RefreshCw size={15} />
        </button>
      </div>

      {activeItems.length === 0 ? (
        <p className="sidebar-empty">활성화된 반복 일정이 없습니다.</p>
      ) : (
        <div className="sidebar-routine-list">
          {activeItems.map((item, index) => (
            <article className="sidebar-routine-item" key={item.repeatId ?? `${item.title}-${item.startDate}-${index}`}>
              <div>
                <strong>{item.title}</strong>
                <span>{item.repeatType === "DAILY" ? "매일" : formatRepeatDays(item.dayOfWeek)}</span>
              </div>
              <div className="sidebar-routine-actions">
                <button
                  type="button"
                  className={item.completedToday ? "completed" : ""}
                  onClick={() => onComplete(item.repeatId)}
                  disabled={item.completedToday}
                  aria-label={`${item.title} 오늘 완료`}
                  title={item.completedToday ? "오늘 완료됨" : "오늘 완료"}
                >
                  <Check size={14} />
                </button>
                <button type="button" onClick={() => onToggle(item.repeatId)} aria-label={`${item.title} 중지`} title="반복 중지">
                  <X size={14} />
                </button>
              </div>
            </article>
          ))}
        </div>
      )}
    </section>
  );
}

function formatRepeatDays(days) {
  if (!Array.isArray(days) || days.length === 0) return "매일";
  const labels = Object.fromEntries(DAYS);
  return days.map((day) => labels[day] ?? day).join(", ");
}
