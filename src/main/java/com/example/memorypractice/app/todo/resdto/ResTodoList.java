package com.example.memorypractice.app.todo.resdto;

import java.util.List;


public record ResTodoList(List<ResTodo> todoList, long totalCount, long completedCount,
                          long totalPages, boolean hasNext, long todayCount, long highCount) {
}
