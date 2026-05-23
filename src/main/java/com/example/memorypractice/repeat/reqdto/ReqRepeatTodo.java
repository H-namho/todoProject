package com.example.memorypractice.repeat.reqdto;

import com.example.memorypractice.repeat.RepeatType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Set;

public record ReqRepeatTodo(@NotBlank String title, @NotBlank String memo, @NotNull RepeatType repeatType,
                            Set<DayOfWeek> dayOfWeek, @NotNull LocalDate startDate, LocalDate endDate) {
}
