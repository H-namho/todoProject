package com.example.memorypratice.todo.reqdto;

import com.example.memorypratice.todo.TodoPriority;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record ReqUpdateTodo(
        @Size(max = 100)
        String title,

        @Size(max = 1000)
        String memo,

        LocalDate dueDate,
        TodoPriority priority
) {
}
