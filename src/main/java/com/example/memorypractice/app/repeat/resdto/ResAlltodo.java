package com.example.memorypractice.app.repeat.resdto;

import com.example.memorypractice.app.repeat.RepeatType;
import com.example.memorypractice.app.todo.TodoPriority;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Set;

public record ResAlltodo(Long todoId, Long repeatId, String title, String memo,
                         boolean completed, TodoPriority priority, LocalDate dueDate,
                         RepeatType repeatType, Set<DayOfWeek> dayOfWeek) {
}
