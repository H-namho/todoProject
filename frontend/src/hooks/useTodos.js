import { useCallback, useEffect, useState } from "react";
import { createTodo, deleteTodo, deleteTodos, getTodos, toggleTodoCompletion, updateTodo } from "../api/todoApi";
import { addDays, emptyTodoForm, today } from "../utils/date";

export default function useTodos(api, isSignedIn, notify) {
  const [todos, setTodos] = useState([]);
  const [summary, setSummary] = useState({ totalCount: 0, completedCount: 0, totalPages: 0, hasNext: false, todayCount: 0, highCount: 0 });
  const [filters, setFilters] = useState({ completed: "", priority: "", keyword: "", page: 0, size: 10 });
  const [viewMode, setViewMode] = useState("all");
  const [todoForm, setTodoForm] = useState(emptyTodoForm());
  const [editingTodo, setEditingTodo] = useState(null);
  const [selectedIds, setSelectedIds] = useState([]);
  const [loading, setLoading] = useState(false);

  const loadTodos = useCallback(async () => {
    if (!isSignedIn) return;
    setLoading(true);
    try {
      const data = await getTodos(api, {
        ...filters,
        viewMode,
        todayDate: today(),
        tomorrowDate: addDays(1),
        upcomingDate: addDays(7),
      });
      setTodos(data.todoList ?? []);
      setSummary({
        totalCount: data.totalCount ?? 0,
        completedCount: data.completedCount ?? 0,
        totalPages: data.totalPages ?? 0,
        hasNext: Boolean(data.hasNext),
        todayCount: data.todayCount ?? 0,
        highCount: data.highCount ?? 0,
      });
    } catch (error) {
      notify(error.message, "error");
    } finally {
      setLoading(false);
    }
  }, [api, filters, isSignedIn, notify, viewMode]);

  useEffect(() => {
    if (!isSignedIn) {
      setTodos([]);
      setSelectedIds([]);
      return;
    }
    loadTodos();
  }, [isSignedIn, loadTodos]);

  function changeViewMode(nextViewMode) {
    setViewMode(nextViewMode);
    setFilters((previous) => ({ ...previous, page: 0 }));
  }

  async function submitTodo(event) {
    event.preventDefault();
    const payload = {
      title: todoForm.title.trim(),
      memo: todoForm.memo.trim(),
      dueDate: todoForm.dueDate,
      todoPriority: todoForm.todoPriority,
    };
    try {
      if (editingTodo) {
        await updateTodo(api, editingTodo.todoId, {
          title: payload.title,
          memo: payload.memo,
          dueDate: payload.dueDate,
          priority: payload.todoPriority,
        });
        notify("할 일을 수정했습니다.");
      } else {
        await createTodo(api, payload);
        notify("할 일을 추가했습니다.");
      }
      setTodoForm(emptyTodoForm());
      setEditingTodo(null);
      loadTodos();
    } catch (error) {
      notify(error.message, "error");
    }
  }

  async function toggleTodo(todoId) {
    const target = todos.find((todo) => todo.todoId === todoId);
    if (!target) return;
    const nextCompleted = !target.completed;
    const completedDelta = nextCompleted ? 1 : -1;
    setTodos((items) => items.map((todo) => (todo.todoId === todoId ? { ...todo, completed: nextCompleted } : todo)));
    setSummary((prev) => ({ ...prev, completedCount: Math.max(0, prev.completedCount + completedDelta) }));
    try {
      await toggleTodoCompletion(api, todoId);
    } catch (error) {
      setTodos((items) => items.map((todo) => (todo.todoId === todoId ? { ...todo, completed: target.completed } : todo)));
      setSummary((prev) => ({ ...prev, completedCount: Math.max(0, prev.completedCount - completedDelta) }));
      notify(error.message, "error");
    }
  }

  async function removeTodo(todoId) {
    try {
      await deleteTodo(api, todoId);
      setSelectedIds((ids) => ids.filter((id) => id !== todoId));
      notify("삭제했습니다.");
      loadTodos();
    } catch (error) {
      notify(error.message, "error");
    }
  }

  async function removeSelected() {
    if (!selectedIds.length) return;
    try {
      await deleteTodos(api, selectedIds);
      setSelectedIds([]);
      notify("선택한 할 일을 삭제했습니다.");
      loadTodos();
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
  }

  function cancelEdit() {
    setEditingTodo(null);
    setTodoForm(emptyTodoForm());
  }

  return {
    visibleTodos: todos,
    summary,
    filters,
    setFilters,
    viewMode,
    setViewMode: changeViewMode,
    todoForm,
    setTodoForm,
    editingTodo,
    selectedIds,
    setSelectedIds,
    loading,
    todayCount: summary.todayCount,
    highCount: summary.highCount,
    loadTodos,
    submitTodo,
    toggleTodo,
    removeTodo,
    removeSelected,
    startEdit,
    cancelEdit,
  };
}
