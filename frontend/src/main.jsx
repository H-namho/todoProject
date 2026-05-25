import React, { useCallback, useEffect, useMemo, useState } from "react";
import { createRoot } from "react-dom/client";
import {
  Bell,
  CalendarDays,
  Check,
  ChevronLeft,
  ChevronRight,
  Circle,
  Edit3,
  Filter,
  LogOut,
  Plus,
  RefreshCw,
  Search,
  Settings,
  Trash2,
  User,
  X,
} from "lucide-react";
import "./styles.css";

const TOKEN_KEY = "memory-practice.tokens";
const PRIORITIES = ["LOW", "MEDIUM", "HIGH"];
const DAYS = [
  ["MONDAY", "월"],
  ["TUESDAY", "화"],
  ["WEDNESDAY", "수"],
  ["THURSDAY", "목"],
  ["FRIDAY", "금"],
  ["SATURDAY", "토"],
  ["SUNDAY", "일"],
];

function readTokens() {
  try {
    return JSON.parse(localStorage.getItem(TOKEN_KEY)) ?? {};
  } catch {
    return {};
  }
}

function saveTokens(tokens) {
  localStorage.setItem(TOKEN_KEY, JSON.stringify(tokens));
}

async function parseResponse(response) {
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

function openNoticeStream({ accessToken, onEvent, onError }) {
  const controller = new AbortController();
  const decoder = new TextDecoder();
  let closed = false;
  let retryTimer = null;

  async function connect() {
    try {
      const response = await fetch("/api/notice/subscribe", {
        headers: {
          Accept: "text/event-stream",
          Authorization: `Bearer ${accessToken}`,
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
      if (!closed && error.name !== "AbortError") {
        onError?.(error);
      }
    }

    if (!closed) {
      retryTimer = window.setTimeout(connect, 3000);
    }
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
    // SSE data can be plain text.
  }

  return { event: eventName, data };
}

function App() {
  const [tokens, setTokens] = useState(readTokens);
  const [profile, setProfile] = useState(null);
  const [authMode, setAuthMode] = useState("signin");
  const [authForm, setAuthForm] = useState({ username: "", password: "", nickname: "" });
  const [todos, setTodos] = useState([]);
  const [summary, setSummary] = useState({ totalCount: 0, completedCount: 0, totalPages: 0, hasNext: false });
  const [filters, setFilters] = useState({ completed: "", priority: "", keyword: "", page: 0, size: 10 });
  const [viewMode, setViewMode] = useState("all");
  const [todoForm, setTodoForm] = useState(emptyTodoForm());
  const [repeatForm, setRepeatForm] = useState(emptyRepeatForm());
  const [repeatItems, setRepeatItems] = useState([]);
  const [editingTodo, setEditingTodo] = useState(null);
  const [selectedIds, setSelectedIds] = useState([]);
  const [noticeItems, setNoticeItems] = useState([]);
  const [profileForm, setProfileForm] = useState({ nickname: "", nowPassword: "", newPassword: "" });
  const [activePanel, setActivePanel] = useState("todo");
  const [toast, setToast] = useState(null);
  const [loading, setLoading] = useState(false);

  const isSignedIn = Boolean(tokens.accessToken);

  const setAuthTokens = useCallback((nextTokens) => {
    saveTokens(nextTokens);
    setTokens(nextTokens);
  }, []);

  const signOutLocal = useCallback(() => {
    localStorage.removeItem(TOKEN_KEY);
    setTokens({});
    setProfile(null);
    setTodos([]);
    setRepeatItems([]);
    setSelectedIds([]);
    setNoticeItems([]);
  }, []);

  const api = useMemo(() => {
    async function request(path, options = {}) {
      const headers = {
        ...(options.body ? { "Content-Type": "application/json" } : {}),
        ...(tokens.accessToken ? { Authorization: `Bearer ${tokens.accessToken}` } : {}),
        ...options.headers,
      };

      const response = await fetch(path, { ...options, headers });

      if (response.status === 401 && tokens.refreshToken) {
        const refreshed = await fetch("/api/user/refresh", {
          method: "POST",
          headers: { "Content-Type": "application/json" },
          body: JSON.stringify({ refreshToken: tokens.refreshToken }),
        });

        if (refreshed.ok) {
          const nextTokens = await refreshed.json();
          setAuthTokens(nextTokens);
          return requestWithToken(path, options, nextTokens.accessToken);
        }

        signOutLocal();
      }

      return parseResponse(response);
    }

    async function requestWithToken(path, options, accessToken) {
      const response = await fetch(path, {
        ...options,
        headers: {
          ...(options.body ? { "Content-Type": "application/json" } : {}),
          Authorization: `Bearer ${accessToken}`,
          ...options.headers,
        },
      });
      return parseResponse(response);
    }

    return { request };
  }, [setAuthTokens, signOutLocal, tokens.accessToken, tokens.refreshToken]);

  const notify = useCallback((message, type = "ok") => {
    setToast({ message, type });
    window.setTimeout(() => setToast(null), 2800);
  }, []);

  const loadProfile = useCallback(async () => {
    if (!isSignedIn) return;
    try {
      const data = await api.request("/api/user/getinfo");
      setProfile(data);
      setProfileForm((prev) => ({ ...prev, nickname: data.nickname ?? "" }));
    } catch (error) {
      notify(error.message, "error");
    }
  }, [api, isSignedIn, notify]);

  const loadTodos = useCallback(async () => {
    if (!isSignedIn) return;
    setLoading(true);
    try {
      const params = new URLSearchParams({
        page: String(filters.page),
        size: String(filters.size),
      });
      if (filters.completed !== "") params.set("completed", filters.completed);
      if (filters.priority) params.set("priority", filters.priority);
      if (filters.keyword.trim()) params.set("keyword", filters.keyword.trim());

      const data = await api.request(`/api/todo/list?${params.toString()}`);
      setTodos(data.todoList ?? []);
      setSummary({
        totalCount: data.totalCount ?? 0,
        completedCount: data.completedCount ?? 0,
        totalPages: data.totalPages ?? 0,
        hasNext: Boolean(data.hasNext),
      });
    } catch (error) {
      notify(error.message, "error");
    } finally {
      setLoading(false);
    }
  }, [api, filters, isSignedIn, notify]);

  const loadRepeats = useCallback(async () => {
    if (!isSignedIn) return;
    try {
      const data = await api.request("/api/repeat/list");
      setRepeatItems(Array.isArray(data) ? data : []);
    } catch (error) {
      notify(error.message, "error");
    }
  }, [api, isSignedIn, notify]);

  useEffect(() => {
    loadProfile();
  }, [loadProfile]);

  useEffect(() => {
    loadTodos();
  }, [loadTodos]);

  useEffect(() => {
    loadRepeats();
  }, [loadRepeats]);

  useEffect(() => {
    if (!tokens.accessToken) return undefined;

    return openNoticeStream({
      accessToken: tokens.accessToken,
      onEvent: ({ event, data }) => {
        if (event === "connect") return;
        if (event !== "today-todo") return;

        const titles = Array.isArray(data) ? data : [String(data)];
        const receivedAt = new Date().toLocaleTimeString("ko-KR", { hour: "2-digit", minute: "2-digit" });

        setNoticeItems((items) =>
          [
            {
              id: `${Date.now()}-${Math.random()}`,
              receivedAt,
              titles,
            },
            ...items,
          ].slice(0, 5),
        );

        notify(`오늘 할 일 ${titles.length}개가 도착했습니다.`, "notice");
        loadTodos();
      },
      onError: () => {
        notify("알림 연결을 다시 시도하고 있습니다.", "error");
      },
    });
  }, [loadTodos, notify, tokens.accessToken]);

  const visibleTodos = useMemo(() => {
    const now = today();
    const inSevenDays = addDays(7);

    return todos.filter((todo) => {
      if (viewMode === "today") return todo.dueDate === now;
      if (viewMode === "upcoming") return !todo.completed && todo.dueDate > now && todo.dueDate <= inSevenDays;
      if (viewMode === "done") return todo.completed;
      return true;
    });
  }, [todos, viewMode]);

  const pageTodayCount = useMemo(() => todos.filter((todo) => todo.dueDate === today()).length, [todos]);
  const pageHighCount = useMemo(() => todos.filter((todo) => todo.priority === "HIGH" && !todo.completed).length, [todos]);

  async function handleAuthSubmit(event) {
    event.preventDefault();
    setLoading(true);
    try {
      if (authMode === "signup") {
        await fetch("/api/user/signup", {
          method: "POST",
          headers: { "Content-Type": "application/json" },
          body: JSON.stringify(authForm),
        }).then(parseResponse);
        notify("가입이 완료됐습니다. 바로 로그인합니다.");
      }

      const nextTokens = await fetch("/api/user/signin", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ username: authForm.username, password: authForm.password }),
      }).then(parseResponse);
      setAuthTokens(nextTokens);
      setAuthForm({ username: "", password: "", nickname: "" });
      setAuthMode("signin");
    } catch (error) {
      notify(error.message, "error");
    } finally {
      setLoading(false);
    }
  }

  async function handleLogout() {
    try {
      if (tokens.accessToken) {
        await api.request("/api/user/logout", { method: "POST" });
      }
    } catch {
      // Token can already be invalid. Local logout is still correct.
    } finally {
      signOutLocal();
      notify("로그아웃했습니다.");
    }
  }

  async function handleTodoSubmit(event) {
    event.preventDefault();
    const payload = {
      title: todoForm.title.trim(),
      memo: todoForm.memo.trim(),
      dueDate: todoForm.dueDate,
      todoPriority: todoForm.todoPriority,
    };

    try {
      if (editingTodo) {
        await api.request(`/api/todo/${editingTodo.todoId}/edit`, {
          method: "PATCH",
          body: JSON.stringify({
            title: payload.title,
            memo: payload.memo,
            dueDate: payload.dueDate,
            priority: payload.todoPriority,
          }),
        });
        notify("할 일을 수정했습니다.");
      } else {
        await api.request("/api/todo/write", {
          method: "POST",
          body: JSON.stringify(payload),
        });
        notify("할 일을 추가했습니다.");
      }

      setTodoForm(emptyTodoForm());
      setEditingTodo(null);
      loadTodos();
    } catch (error) {
      notify(error.message, "error");
    }
  }

  async function handleRepeatSubmit(event) {
    event.preventDefault();
    try {
      await api.request("/api/repeat/write", {
        method: "POST",
        body: JSON.stringify({
          title: repeatForm.title.trim(),
          memo: repeatForm.memo.trim(),
          repeatType: repeatForm.repeatType,
          dayOfWeek: repeatForm.repeatType === "WEEKLY" ? repeatForm.dayOfWeek : [],
          startDate: repeatForm.startDate,
          endDate: repeatForm.endDate || null,
        }),
      });
      setRepeatForm(emptyRepeatForm());
      notify("반복 할 일을 만들었습니다.");
      loadRepeats();
      loadTodos();
    } catch (error) {
      notify(error.message, "error");
    }
  }

  async function toggleRepeat(repeatId) {
    const target = repeatItems.find((item) => item.repeatId === repeatId);
    if (!target) return;

    const nextActive = !target.active;
    setRepeatItems((items) =>
      items.map((item) => (item.repeatId === repeatId ? { ...item, active: nextActive } : item)),
    );

    try {
      await api.request(`/api/repeat/${repeatId}/active`, { method: "PATCH" });
      notify(nextActive ? "반복 일정을 다시 활성화했습니다." : "반복 일정을 중지했습니다.");
    } catch (error) {
      setRepeatItems((items) =>
        items.map((item) => (item.repeatId === repeatId ? { ...item, active: target.active } : item)),
      );
      notify(error.message, "error");
    }
  }

  async function completeRepeat(repeatId) {
    const target = repeatItems.find((item) => item.repeatId === repeatId);
    if (!target || target.completedToday) return;

    setRepeatItems((items) =>
      items.map((item) => (item.repeatId === repeatId ? { ...item, completedToday: true } : item)),
    );

    try {
      await api.request(`/api/repeat/${repeatId}/completed`, { method: "PATCH" });
      notify("오늘의 반복 할 일을 완료했습니다.");
    } catch (error) {
      setRepeatItems((items) =>
        items.map((item) => (item.repeatId === repeatId ? { ...item, completedToday: false } : item)),
      );
      notify(error.message, "error");
    }
  }

  async function toggleTodo(todoId) {
    const target = todos.find((todo) => todo.todoId === todoId);
    if (!target) return;

    const nextCompleted = !target.completed;
    const completedDelta = nextCompleted ? 1 : -1;

    setTodos((items) => items.map((todo) => (todo.todoId === todoId ? { ...todo, completed: nextCompleted } : todo)));
    setSummary((prev) => ({
      ...prev,
      completedCount: Math.max(0, prev.completedCount + completedDelta),
    }));

    try {
      await api.request(`/api/todo/${todoId}/chk`, { method: "PATCH" });
    } catch (error) {
      setTodos((items) => items.map((todo) => (todo.todoId === todoId ? { ...todo, completed: target.completed } : todo)));
      setSummary((prev) => ({
        ...prev,
        completedCount: Math.max(0, prev.completedCount - completedDelta),
      }));
      notify(error.message, "error");
    }
  }

  async function deleteTodo(todoId) {
    try {
      await api.request(`/api/todo/${todoId}`, { method: "DELETE" });
      setSelectedIds((ids) => ids.filter((id) => id !== todoId));
      notify("삭제했습니다.");
      loadTodos();
    } catch (error) {
      notify(error.message, "error");
    }
  }

  async function deleteSelected() {
    if (!selectedIds.length) return;
    try {
      await api.request("/api/todo/delete/todos", {
        method: "DELETE",
        body: JSON.stringify({ todoIds: selectedIds }),
      });
      setSelectedIds([]);
      notify("선택한 할 일을 삭제했습니다.");
      loadTodos();
    } catch (error) {
      notify(error.message, "error");
    }
  }

  async function updateNickname(event) {
    event.preventDefault();
    try {
      await api.request("/api/user/edit", {
        method: "PATCH",
        body: JSON.stringify({ nickname: profileForm.nickname }),
      });
      notify("닉네임을 저장했습니다.");
      loadProfile();
    } catch (error) {
      notify(error.message, "error");
    }
  }

  async function updatePassword(event) {
    event.preventDefault();
    try {
      await api.request("/api/user/editPw", {
        method: "PATCH",
        body: JSON.stringify({
          nowPassword: profileForm.nowPassword,
          newPassword: profileForm.newPassword,
        }),
      });
      setProfileForm((prev) => ({ ...prev, nowPassword: "", newPassword: "" }));
      notify("비밀번호를 변경했습니다.");
    } catch (error) {
      notify(error.message, "error");
    }
  }

  function startEdit(todo) {
    setEditingTodo(todo);
    setTodoForm({
      title: todo.title ?? "",
      memo: todo.memo ?? "",
      dueDate: todo.dueDate ?? today(),
      todoPriority: todo.priority ?? "MEDIUM",
    });
    setActivePanel("todo");
  }

  if (!isSignedIn) {
    return (
      <main className="auth-shell">
        <section className="auth-panel">
          <div>
            <p className="eyebrow">Memory Practice</p>
            <h1>반복과 오늘 할 일을 한 화면에서 관리하세요</h1>
            <p className="auth-copy">로그인하면 개인 할 일, 반복 일정, 우선순위 필터와 실시간 알림을 바로 사용할 수 있습니다.</p>
          </div>

          <form className="auth-card" onSubmit={handleAuthSubmit}>
            <div className="mode-switch" aria-label="인증 모드">
              <button type="button" className={authMode === "signin" ? "active" : ""} onClick={() => setAuthMode("signin")}>
                로그인
              </button>
              <button type="button" className={authMode === "signup" ? "active" : ""} onClick={() => setAuthMode("signup")}>
                회원가입
              </button>
            </div>

            <label>
              아이디
              <input value={authForm.username} onChange={(event) => setAuthForm({ ...authForm, username: event.target.value })} required />
            </label>
            <label>
              비밀번호
              <input
                type="password"
                value={authForm.password}
                onChange={(event) => setAuthForm({ ...authForm, password: event.target.value })}
                required
              />
            </label>
            {authMode === "signup" && (
              <label>
                닉네임
                <input value={authForm.nickname} onChange={(event) => setAuthForm({ ...authForm, nickname: event.target.value })} required />
              </label>
            )}
            <button className="primary-button" disabled={loading}>
              {loading ? "처리 중" : authMode === "signin" ? "로그인" : "가입하고 시작"}
            </button>
          </form>
        </section>
        <Toast toast={toast} />
      </main>
    );
  }

  return (
    <main className="app-shell">
      <aside className="sidebar">
        <div>
          <p className="eyebrow">Memory Practice</p>
          <h1>Todo Desk</h1>
        </div>

        <div className="profile-box">
          <div className="avatar">
            <User size={20} />
          </div>
          <div>
            <strong>{profile?.nickname ?? "사용자"}</strong>
            <span>{profile?.username}</span>
          </div>
        </div>

        <SidebarRoutine items={repeatItems} onRefresh={loadRepeats} onToggle={toggleRepeat} onComplete={completeRepeat} />

        <nav className="nav-list">
          <button className={activePanel === "todo" ? "active" : ""} onClick={() => setActivePanel("todo")}>
            <Plus size={18} /> 할 일 작성
          </button>
          <button className={activePanel === "repeat" ? "active" : ""} onClick={() => setActivePanel("repeat")}>
            <RefreshCw size={18} /> 반복 관리
          </button>
          <button className={activePanel === "settings" ? "active" : ""} onClick={() => setActivePanel("settings")}>
            <Settings size={18} /> 계정 설정
          </button>
        </nav>

        <button className="ghost-button" onClick={handleLogout}>
          <LogOut size={18} /> 로그아웃
        </button>
      </aside>

      <section className="workspace">
        <header className="topbar">
          <div>
            <p className="eyebrow">Dashboard</p>
            <h2>오늘의 작업 흐름</h2>
          </div>
          <div className="metric-row">
            <Metric label="전체" value={summary.totalCount} />
            <Metric label="완료" value={summary.completedCount} />
            <Metric label="오늘" value={pageTodayCount} />
            <Metric label="높음" value={pageHighCount} />
          </div>
        </header>

        {noticeItems.length > 0 && (
          <section className="notice-strip" aria-live="polite">
            <div className="notice-title">
              <Bell size={18} />
              <strong>실시간 알림</strong>
            </div>
            <div className="notice-list">
              {noticeItems.map((notice) => (
                <div className="notice-item" key={notice.id}>
                  <span>{notice.receivedAt}</span>
                  <p>{notice.titles.join(", ")}</p>
                </div>
              ))}
            </div>
          </section>
        )}

        <div className="content-grid">
          <section className="panel main-panel">
            <div className="panel-head">
              <div>
                <p className="eyebrow">Tasks</p>
                <h3>할 일 목록</h3>
              </div>
              <button className="icon-button" onClick={loadTodos} aria-label="새로고침" title="새로고침">
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
                <button className="danger-button" onClick={deleteSelected}>
                  <Trash2 size={16} /> 선택 삭제
                </button>
              </div>
            )}

            <div className="todo-list" aria-busy={loading}>
              {visibleTodos.length === 0 && <EmptyState loading={loading} />}
              {visibleTodos.map((todo) => (
                <article key={todo.todoId} className={`todo-item ${todo.completed ? "done" : ""}`}>
                  <input
                    type="checkbox"
                    checked={selectedIds.includes(todo.todoId)}
                    onChange={(event) =>
                      setSelectedIds((ids) =>
                        event.target.checked ? [...ids, todo.todoId] : ids.filter((id) => id !== todo.todoId),
                      )
                    }
                    aria-label={`${todo.title} 선택`}
                  />
                  <button className="complete-button" onClick={() => toggleTodo(todo.todoId)} aria-label="완료 전환">
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
                    <button className="icon-button" onClick={() => startEdit(todo)} aria-label="수정" title="수정">
                      <Edit3 size={17} />
                    </button>
                    <button className="icon-button danger" onClick={() => deleteTodo(todo.todoId)} aria-label="삭제" title="삭제">
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

          <aside className="panel side-panel">
            {activePanel === "todo" && (
              <TodoForm
                form={todoForm}
                setForm={setTodoForm}
                editingTodo={editingTodo}
                cancelEdit={() => {
                  setEditingTodo(null);
                  setTodoForm(emptyTodoForm());
                }}
                onSubmit={handleTodoSubmit}
              />
            )}
            {activePanel === "repeat" && (
              <RepeatPanel form={repeatForm} setForm={setRepeatForm} onSubmit={handleRepeatSubmit} />
            )}
            {activePanel === "settings" && (
              <SettingsPanel
                profileForm={profileForm}
                setProfileForm={setProfileForm}
                updateNickname={updateNickname}
                updatePassword={updatePassword}
              />
            )}
          </aside>
        </div>
      </section>
      <Toast toast={toast} />
    </main>
  );
}

function TodoForm({ form, setForm, editingTodo, cancelEdit, onSubmit }) {
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

function RepeatPanel({ form, setForm, onSubmit }) {
  return (
    <div className="repeat-panel-stack">
      <RepeatForm form={form} setForm={setForm} onSubmit={onSubmit} />
    </div>
  );
}

function SidebarRoutine({ items, onRefresh, onToggle, onComplete }) {
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

function RepeatForm({ form, setForm, onSubmit }) {
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

function SettingsPanel({ profileForm, setProfileForm, updateNickname, updatePassword }) {
  return (
    <div className="settings-stack">
      <form className="stack-form" onSubmit={updateNickname}>
        <div>
          <p className="eyebrow">Profile</p>
          <h3>닉네임</h3>
        </div>
        <label>
          닉네임
          <input value={profileForm.nickname} onChange={(event) => setProfileForm({ ...profileForm, nickname: event.target.value })} required />
        </label>
        <button className="primary-button">저장</button>
      </form>
      <form className="stack-form compact" onSubmit={updatePassword}>
        <div>
          <p className="eyebrow">Security</p>
          <h3>비밀번호</h3>
        </div>
        <label>
          현재 비밀번호
          <input
            type="password"
            value={profileForm.nowPassword}
            onChange={(event) => setProfileForm({ ...profileForm, nowPassword: event.target.value })}
            required
          />
        </label>
        <label>
          새 비밀번호
          <input
            type="password"
            value={profileForm.newPassword}
            onChange={(event) => setProfileForm({ ...profileForm, newPassword: event.target.value })}
            minLength={8}
            required
          />
        </label>
        <button className="primary-button">변경</button>
      </form>
    </div>
  );
}

function Metric({ label, value }) {
  return (
    <div className="metric">
      <span>{label}</span>
      <strong>{value}</strong>
    </div>
  );
}

function EmptyState({ loading }) {
  return (
    <div className="empty-state">
      <CalendarDays size={34} />
      <strong>{loading ? "불러오는 중" : "표시할 할 일이 없습니다"}</strong>
      <span>{loading ? "잠시만 기다려주세요." : "필터를 바꾸거나 새 할 일을 추가하세요."}</span>
    </div>
  );
}

function Toast({ toast }) {
  if (!toast) return null;
  return <div className={`toast ${toast.type}`}>{toast.message}</div>;
}

function formatRepeatDays(days) {
  if (!Array.isArray(days) || days.length === 0) return "매일";
  const labels = Object.fromEntries(DAYS);
  return days.map((day) => labels[day] ?? day).join(", ");
}

function emptyTodoForm() {
  return { title: "", memo: "", dueDate: today(), todoPriority: "MEDIUM" };
}

function emptyRepeatForm() {
  return { title: "", memo: "", repeatType: "DAILY", dayOfWeek: [], startDate: today(), endDate: "" };
}

function today() {
  return new Date().toISOString().slice(0, 10);
}

function addDays(days) {
  const date = new Date();
  date.setDate(date.getDate() + days);
  return date.toISOString().slice(0, 10);
}

createRoot(document.getElementById("root")).render(<App />);
