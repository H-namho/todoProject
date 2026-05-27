import { useState } from "react";
import AuthScreen from "./components/auth/AuthScreen";
import Toast from "./components/common/Toast";
import NoticeStrip from "./components/layout/NoticeStrip";
import Sidebar from "./components/layout/Sidebar";
import Topbar from "./components/layout/Topbar";
import RepeatManager from "./components/repeat/RepeatManager";
import SettingsPanel from "./components/settings/SettingsPanel";
import TodoForm from "./components/todo/TodoForm";
import TodoList from "./components/todo/TodoList";
import useAuth from "./hooks/useAuth";
import useNotices from "./hooks/useNotices";
import useRepeats from "./hooks/useRepeats";
import useToast from "./hooks/useToast";
import useTodos from "./hooks/useTodos";

export default function App() {
  const { toast, notify } = useToast();
  const auth = useAuth(notify);
  const todos = useTodos(auth.api, auth.isSignedIn, notify);
  const repeats = useRepeats(auth.api, auth.isSignedIn, notify, todos.loadTodos);
  const notices = useNotices(auth.api, auth.isSignedIn, notify, todos.loadTodos);
  const [activePanel, setActivePanel] = useState("todo");

  if (!auth.isSignedIn) {
    return (
      <AuthScreen
        authMode={auth.authMode}
        setAuthMode={auth.setAuthMode}
        authForm={auth.authForm}
        setAuthForm={auth.setAuthForm}
        loading={auth.authLoading}
        onSubmit={auth.handleAuthSubmit}
        toast={toast}
      />
    );
  }

  return (
    <main className="app-shell">
      <Sidebar
        profile={auth.profile}
        repeats={repeats.repeatItems}
        activePanel={activePanel}
        setActivePanel={setActivePanel}
        onRefreshRepeats={repeats.loadRepeats}
        onToggleRepeat={repeats.toggleRepeat}
        onCompleteRepeat={repeats.completeRepeat}
        onLogout={auth.handleLogout}
      />
      <section className="workspace">
        <Topbar summary={todos.summary} todayCount={todos.todayCount} highCount={todos.highCount} />
        <NoticeStrip notices={notices} />
        <div className="content-grid">
          <TodoList
            todos={todos.visibleTodos}
            loading={todos.loading}
            summary={todos.summary}
            filters={todos.filters}
            setFilters={todos.setFilters}
            viewMode={todos.viewMode}
            setViewMode={todos.setViewMode}
            selectedIds={todos.selectedIds}
            setSelectedIds={todos.setSelectedIds}
            onRefresh={todos.loadTodos}
            onToggle={todos.toggleTodo}
            onEdit={(todo) => {
              todos.startEdit(todo);
              setActivePanel("todo");
            }}
            onDelete={todos.removeTodo}
            onDeleteSelected={todos.removeSelected}
          />
          <aside className="panel side-panel">
            {activePanel === "todo" && (
              <TodoForm
                form={todos.todoForm}
                setForm={todos.setTodoForm}
                editingTodo={todos.editingTodo}
                cancelEdit={todos.cancelEdit}
                onSubmit={todos.submitTodo}
              />
            )}
            {activePanel === "repeat" && (
              <RepeatManager
                form={repeats.repeatForm}
                setForm={repeats.setRepeatForm}
                items={repeats.repeatItems}
                editingRepeat={repeats.editingRepeat}
                selectedRepeatId={repeats.selectedRepeatId}
                detailMonth={repeats.detailMonth}
                detail={repeats.detail}
                onSubmit={repeats.submitRepeat}
                onCancelEdit={repeats.cancelEdit}
                onStartEdit={repeats.startEdit}
                onToggleActive={repeats.toggleRepeat}
                onToggleComplete={repeats.completeRepeat}
                onDelete={repeats.removeRepeat}
                onSelect={repeats.selectRepeat}
                onChangeMonth={repeats.changeDetailMonth}
              />
            )}
            {activePanel === "settings" && (
              <SettingsPanel
                profileForm={auth.profileForm}
                setProfileForm={auth.setProfileForm}
                onUpdateNickname={auth.handleUpdateNickname}
                onUpdatePassword={auth.handleUpdatePassword}
              />
            )}
          </aside>
        </div>
      </section>
      <Toast toast={toast} />
    </main>
  );
}
