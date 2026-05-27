package com.example.memorypractice.app.todo.reqdto;

import com.example.memorypractice.app.todo.TodoPriority;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record ReqWriteTodo(@NotBlank String title,
                           @NotBlank String memo,
                           @NotNull LocalDate dueDate,
                           @NotNull TodoPriority todoPriority) {
}
