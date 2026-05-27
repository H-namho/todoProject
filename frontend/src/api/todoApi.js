export function getTodos(api, filters) {
  const params = new URLSearchParams({
    page: String(filters.page),
    size: String(filters.size),
  });
  if (filters.viewMode === "done") {
    params.set("completed", "true");
  } else if (filters.viewMode === "upcoming") {
    params.set("completed", "false");
  } else if (filters.completed !== "") {
    params.set("completed", filters.completed);
  }
  if (filters.viewMode === "today") {
    params.set("dueDateFrom", filters.todayDate);
    params.set("dueDateTo", filters.todayDate);
  }
  if (filters.viewMode === "upcoming") {
    params.set("dueDateFrom", filters.tomorrowDate);
    params.set("dueDateTo", filters.upcomingDate);
  }
  if (filters.priority) params.set("priority", filters.priority);
  if (filters.keyword.trim()) params.set("keyword", filters.keyword.trim());
  return api.request(`/api/todo/list?${params.toString()}`);
}

export function createTodo(api, form) {
  return api.request("/api/todo/write", {
    method: "POST",
    body: JSON.stringify(form),
  });
}

export function updateTodo(api, todoId, form) {
  return api.request(`/api/todo/${todoId}/edit`, {
    method: "PATCH",
    body: JSON.stringify(form),
  });
}

export function toggleTodoCompletion(api, todoId) {
  return api.request(`/api/todo/${todoId}/chk`, { method: "PATCH" });
}

export function deleteTodo(api, todoId) {
  return api.request(`/api/todo/${todoId}`, { method: "DELETE" });
}

export function deleteTodos(api, todoIds) {
  return api.request("/api/todo/delete/todos", {
    method: "DELETE",
    body: JSON.stringify({ todoIds }),
  });
}
