package com.example.memorypractice.todo.resdto;

import java.util.List;


public record ResTodoList(List<ResTodo> todoList, long totalCount, long completedCount,
                          long totalPages, boolean hasNext) {
}
