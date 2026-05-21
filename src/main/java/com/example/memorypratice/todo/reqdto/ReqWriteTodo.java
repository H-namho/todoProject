package com.example.memorypratice.todo.reqdto;

import com.example.memorypratice.todo.TodoPriority;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record ReqWriteTodo(@NotBlank String title,
                           @NotBlank String memo,
                           @NotNull LocalDate dueDate,
                           @NotNull TodoPriority todoPriority) {
}
