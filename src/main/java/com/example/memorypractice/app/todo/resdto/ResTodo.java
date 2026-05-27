package com.example.memorypractice.app.todo.resdto;

import com.example.memorypractice.app.todo.TodoPriority;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record ResTodo(Long todoId, String title, String memo,boolean completed ,TodoPriority priority
        , LocalDateTime createAt, LocalDate dueDate) {
}
