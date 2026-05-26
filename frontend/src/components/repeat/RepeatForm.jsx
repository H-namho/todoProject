import { RefreshCw, X } from "lucide-react";
import { DAYS } from "../../constants/options";

export default function RepeatForm({ form, setForm, editingRepeat, onCancel, onSubmit }) {
  function toggleDay(day) {
    const next = form.dayOfWeek.includes(day) ? form.dayOfWeek.filter((item) => item !== day) : [...form.dayOfWeek, day];
    setForm({ ...form, dayOfWeek: next });
  }

  return (
    <form className="stack-form" onSubmit={onSubmit}>
      <div className="panel-head">
        <div>
          <p className="eyebrow">Routine</p>
          <h3>{editingRepeat ? editingRepeat.title : "새 반복 할 일"}</h3>
        </div>
        {editingRepeat && (
          <button type="button" className="icon-button" onClick={onCancel} aria-label="수정 취소" title="수정 취소">
            <X size={18} />
          </button>
        )}
      </div>
      {!editingRepeat && (
        <>
          <label>
            제목
            <input value={form.title} onChange={(event) => setForm({ ...form, title: event.target.value })} required maxLength={100} />
          </label>
          <label>
            메모
            <textarea value={form.memo} onChange={(event) => setForm({ ...form, memo: event.target.value })} required maxLength={1000} rows={4} />
          </label>
        </>
      )}
      <label>
        반복 방식
        <select
          value={form.repeatType}
          onChange={(event) =>
            setForm({ ...form, repeatType: event.target.value, dayOfWeek: event.target.value === "DAILY" ? [] : form.dayOfWeek })
          }
        >
          <option value="DAILY">매일</option>
          <option value="WEEKLY">매주</option>
        </select>
      </label>
      {form.repeatType === "WEEKLY" && (
        <div className="day-grid">
          {DAYS.map(([value, label]) => (
            <button type="button" key={value} className={form.dayOfWeek.includes(value) ? "active" : ""} onClick={() => toggleDay(value)}>
              {label}
            </button>
          ))}
        </div>
      )}
      <div className="form-pair">
        <label>
          시작일
          <input type="date" value={form.startDate} onChange={(event) => setForm({ ...form, startDate: event.target.value })} required />
        </label>
        <label>
          종료일
          <input type="date" value={form.endDate} onChange={(event) => setForm({ ...form, endDate: event.target.value })} />
        </label>
      </div>
      <button className="primary-button">
        <RefreshCw size={18} /> {editingRepeat ? "일정 저장" : "반복 생성"}
      </button>
    </form>
  );
}
