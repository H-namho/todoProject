import { Plus, X } from "lucide-react";
import { PRIORITIES } from "../../constants/options";

export default function TodoForm({ form, setForm, editingTodo, cancelEdit, onSubmit }) {
  return (
    <form className="stack-form" onSubmit={onSubmit}>
      <div className="panel-head">
        <div>
          <p className="eyebrow">Compose</p>
          <h3>{editingTodo ? "할 일 수정" : "새 할 일"}</h3>
        </div>
        {editingTodo && (
          <button type="button" className="icon-button" onClick={cancelEdit} aria-label="수정 취소" title="수정 취소">
            <X size={18} />
          </button>
        )}
      </div>
      <label>
        제목
        <input value={form.title} onChange={(event) => setForm({ ...form, title: event.target.value })} required maxLength={100} />
      </label>
      <label>
        메모
        <textarea value={form.memo} onChange={(event) => setForm({ ...form, memo: event.target.value })} required maxLength={1000} rows={5} />
      </label>
      <div className="form-pair">
        <label>
          마감일
          <input type="date" value={form.dueDate} onChange={(event) => setForm({ ...form, dueDate: event.target.value })} required />
        </label>
        <label>
          우선순위
          <select value={form.todoPriority} onChange={(event) => setForm({ ...form, todoPriority: event.target.value })}>
            {PRIORITIES.map((priority) => (
              <option key={priority} value={priority}>
                {priority}
              </option>
            ))}
          </select>
        </label>
      </div>
      <button className="primary-button">
        <Plus size={18} /> {editingTodo ? "수정 저장" : "추가"}
      </button>
    </form>
  );
}
