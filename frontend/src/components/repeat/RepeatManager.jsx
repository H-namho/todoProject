import { BarChart3, CalendarDays, Check, Circle, Edit3, PauseCircle, PlayCircle, Trash2 } from "lucide-react";
import { DAYS } from "../../constants/options";
import { isRepeatDueToday } from "../../utils/date";
import RepeatForm from "./RepeatForm";

export default function RepeatManager({
  form,
  setForm,
  items,
  editingRepeat,
  selectedRepeatId,
  detailMonth,
  detail,
  onSubmit,
  onCancelEdit,
  onStartEdit,
  onToggleActive,
  onToggleComplete,
  onDelete,
  onSelect,
  onChangeMonth,
}) {
  const selectedRepeat = items.find(function findSelected(item) {
    return item.repeatId === selectedRepeatId;
  });

  return (
    <div className="repeat-panel-stack">
      <RepeatForm form={form} setForm={setForm} editingRepeat={editingRepeat} onCancel={onCancelEdit} onSubmit={onSubmit} />

      <section className="repeat-list-panel">
        <div className="panel-head">
          <div>
            <p className="eyebrow">Routines</p>
            <h3>루틴 관리</h3>
          </div>
        </div>
        {items.length === 0 ? (
          <p className="repeat-empty">등록한 반복 일정이 없습니다.</p>
        ) : (
          <div className="repeat-card-list">
            {items.map((item) => (
              <article className="repeat-card" key={item.repeatId}>
                <div>
                  <strong>{item.title}</strong>
                  <span className={`status-pill ${item.active ? "active" : ""}`}>{item.active ? "활성" : "중지"}</span>
                </div>
                <p>{item.memo}</p>
                <div className="repeat-meta">
                  <span>{item.repeatType === "DAILY" ? "매일" : formatRepeatDays(item.dayOfWeek)}</span>
                  <span>{isRepeatDueToday(item) ? "오늘 수행" : formatPeriod(item.startDate, item.endDate)}</span>
                </div>
                <div className="repeat-actions">
                  <button
                    type="button"
                    className={`icon-button ${item.completedToday ? "checked" : ""}`}
                    onClick={() => onToggleComplete(item.repeatId)}
                    disabled={!item.completedToday && !isRepeatDueToday(item)}
                    aria-label={item.completedToday ? "오늘 완료 취소" : "오늘 완료"}
                    title={item.completedToday ? "오늘 완료 취소" : isRepeatDueToday(item) ? "오늘 완료" : "오늘 수행 대상이 아닙니다"}
                  >
                    {item.completedToday ? <Check size={17} /> : <Circle size={17} />}
                  </button>
                  <button type="button" className="icon-button" onClick={() => onSelect(item.repeatId)} aria-label="달성 현황" title="달성 현황">
                    <BarChart3 size={17} />
                  </button>
                  <button type="button" className="icon-button" onClick={() => onStartEdit(item)} aria-label="일정 수정" title="일정 수정">
                    <Edit3 size={17} />
                  </button>
                  <button type="button" className="icon-button" onClick={() => onToggleActive(item.repeatId)} aria-label="활성 상태 변경" title="활성 상태 변경">
                    {item.active ? <PauseCircle size={17} /> : <PlayCircle size={17} />}
                  </button>
                  <button type="button" className="icon-button danger" onClick={() => onDelete(item.repeatId)} aria-label="삭제" title="삭제">
                    <Trash2 size={17} />
                  </button>
                </div>
              </article>
            ))}
          </div>
        )}
      </section>

      {selectedRepeat && (
        <section className="repeat-insight">
          <div className="panel-head">
            <div>
              <p className="eyebrow">Progress</p>
              <h3>{selectedRepeat.title}</h3>
            </div>
            <label className="month-field">
              <CalendarDays size={15} />
              <input type="month" value={detailMonth} onChange={(event) => onChangeMonth(event.target.value)} />
            </label>
          </div>
          {detail.loading ? (
            <p className="repeat-empty">기록을 불러오는 중입니다.</p>
          ) : (
            <>
              <div className="repeat-metrics">
                <div>
                  <span>예정</span>
                  <strong>{detail.monthlyCount?.expectedCount ?? 0}</strong>
                </div>
                <div>
                  <span>완료</span>
                  <strong>{detail.monthlyCount?.completedCount ?? 0}</strong>
                </div>
                <div>
                  <span>달성률</span>
                  <strong>{achievementRate(detail.monthlyCount)}%</strong>
                </div>
              </div>
              <div className="completion-history">
                <strong>최근 완료일</strong>
                {detail.completedDays.length === 0 ? (
                  <span>완료 기록 없음</span>
                ) : (
                  <div>
                    {detail.completedDays.slice(0, 6).map((day) => (
                      <span key={day.completeDay}>{day.completeDay}</span>
                    ))}
                  </div>
                )}
              </div>
            </>
          )}
        </section>
      )}
    </div>
  );
}

function formatRepeatDays(days) {
  if (!Array.isArray(days) || days.length === 0) return "요일 미지정";

  const orderedLabels = [];
  // 서버 응답 Set의 순서와 관계없이 화면에는 월요일부터 표시한다.
  for (const [value, label] of DAYS) {
    if (days.includes(value)) {
      orderedLabels.push(label);
    }
  }
  return orderedLabels.join(", ");
}

function formatPeriod(startDate, endDate) {
  return `${startDate} - ${endDate ?? "계속"}`;
}

function achievementRate(monthlyCount) {
  if (!monthlyCount || monthlyCount.expectedCount === 0) return 0;
  return Math.round((monthlyCount.completedCount / monthlyCount.expectedCount) * 100);
}
