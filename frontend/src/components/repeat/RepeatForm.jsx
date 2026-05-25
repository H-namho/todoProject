import { RefreshCw } from "lucide-react";
import { DAYS } from "../../constants/options";

export default function RepeatForm({ form, setForm, onSubmit }) {
  function toggleDay(day) {
    const next = form.dayOfWeek.includes(day) ? form.dayOfWeek.filter((item) => item !== day) : [...form.dayOfWeek, day];
    setForm({ ...form, dayOfWeek: next });
  }

  return (
    <form className="stack-form" onSubmit={onSubmit}>
      <div>
        <p className="eyebrow">Routine</p>
        <h3>반복 할 일</h3>
      </div>
      <label>
        제목
        <input value={form.title} onChange={(event) => setForm({ ...form, title: event.target.value })} required />
      </label>
      <label>
        메모
        <textarea value={form.memo} onChange={(event) => setForm({ ...form, memo: event.target.value })} required rows={4} />
      </label>
      <label>
        반복 방식
        <select value={form.repeatType} onChange={(event) => setForm({ ...form, repeatType: event.target.value })}>
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
        <RefreshCw size={18} /> 반복 생성
      </button>
    </form>
  );
}
