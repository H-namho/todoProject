import { Bell, CalendarDays, Check, ChevronLeft, ChevronRight, Circle, Edit3, Filter, RefreshCw, Search, Trash2 } from "lucide-react";
import { PRIORITIES } from "../../constants/options";
import EmptyState from "../common/EmptyState";

export default function TodoList({
  todos,
  loading,
  summary,
  filters,
  setFilters,
  viewMode,
  setViewMode,
  selectedIds,
  setSelectedIds,
  onRefresh,
  onToggle,
  onEdit,
  onDelete,
  onDeleteSelected,
}) {
  return (
    <section className="panel main-panel">
      <div className="panel-head">
        <div>
          <p className="eyebrow">Tasks</p>
          <h3>할 일 목록</h3>
        </div>
        <button className="icon-button" onClick={onRefresh} aria-label="새로고침" title="새로고침">
          <RefreshCw size={18} />
        </button>
      </div>
      <div className="view-tabs" aria-label="목록 보기">
        {[
          ["all", "전체"],
          ["today", "오늘"],
          ["upcoming", "7일 내"],
          ["done", "완료"],
        ].map(([value, label]) => (
          <button key={value} className={viewMode === value ? "active" : ""} onClick={() => setViewMode(value)}>
            {label}
          </button>
        ))}
      </div>
      <div className="filters">
        <label className="search-field">
          <Search size={18} />
          <input
            value={filters.keyword}
            onChange={(event) => setFilters({ ...filters, keyword: event.target.value, page: 0 })}
            placeholder="제목이나 메모 검색"
          />
        </label>
        <label>
          <Filter size={16} />
          <select value={filters.completed} onChange={(event) => setFilters({ ...filters, completed: event.target.value, page: 0 })}>
            <option value="">전체 상태</option>
            <option value="false">진행 중</option>
            <option value="true">완료</option>
          </select>
        </label>
        <label>
          <Bell size={16} />
          <select value={filters.priority} onChange={(event) => setFilters({ ...filters, priority: event.target.value, page: 0 })}>
            <option value="">전체 우선순위</option>
            {PRIORITIES.map((priority) => (
              <option key={priority} value={priority}>
                {priority}
              </option>
            ))}
          </select>
        </label>
      </div>
      {selectedIds.length > 0 && (
        <div className="bulk-bar">
          <span>{selectedIds.length}개 선택됨</span>
          <button className="danger-button" onClick={onDeleteSelected}>
            <Trash2 size={16} /> 선택 삭제
          </button>
        </div>
      )}
      <div className="todo-list" aria-busy={loading}>
        {todos.length === 0 && <EmptyState loading={loading} />}
        {todos.map((todo) => (
          <article key={todo.todoId} className={`todo-item ${todo.completed ? "done" : ""}`}>
            <input
              type="checkbox"
              checked={selectedIds.includes(todo.todoId)}
              onChange={(event) =>
                setSelectedIds((ids) => (event.target.checked ? [...ids, todo.todoId] : ids.filter((id) => id !== todo.todoId)))
              }
              aria-label={`${todo.title} 선택`}
            />
            <button className="complete-button" onClick={() => onToggle(todo.todoId)} aria-label="완료 전환">
              {todo.completed ? <Check size={18} /> : <Circle size={18} />}
            </button>
            <div className="todo-body">
              <div className="todo-title-row">
                <strong>{todo.title}</strong>
                <span className={`priority ${todo.priority?.toLowerCase()}`}>{todo.priority}</span>
              </div>
              <p>{todo.memo}</p>
              <span className="date-line">
                <CalendarDays size={14} /> {todo.dueDate ?? "날짜 없음"}
              </span>
            </div>
            <div className="row-actions">
              <button className="icon-button" onClick={() => onEdit(todo)} aria-label="수정" title="수정">
                <Edit3 size={17} />
              </button>
              <button className="icon-button danger" onClick={() => onDelete(todo.todoId)} aria-label="삭제" title="삭제">
                <Trash2 size={17} />
              </button>
            </div>
          </article>
        ))}
      </div>
      <footer className="pagination">
        <button
          className="icon-button"
          disabled={filters.page === 0}
          onClick={() => setFilters({ ...filters, page: Math.max(0, filters.page - 1) })}
          aria-label="이전 페이지"
        >
          <ChevronLeft size={18} />
        </button>
        <span>
          {filters.page + 1} / {Math.max(1, summary.totalPages)}
        </span>
        <button
          className="icon-button"
          disabled={!summary.hasNext}
          onClick={() => setFilters({ ...filters, page: filters.page + 1 })}
          aria-label="다음 페이지"
        >
          <ChevronRight size={18} />
        </button>
      </footer>
    </section>
  );
}
