package com.example.memorypractice.todo.resdto;

import com.example.memorypractice.todo.TodoPriority;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record ResTodo(Long todoId, String title, String memo,boolean completed ,TodoPriority priority
        , LocalDateTime createAt, LocalDate dueDate) {
}
