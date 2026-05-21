package com.example.memorypratice.todo.reqdto;

import com.example.memorypratice.todo.TodoPriority;

import java.time.LocalDate;

public record ReqUpdateTodo(
        String title,
        String memo,
        Boolean completed,
        LocalDate dueDate,
        TodoPriority priority
) {
}
