package com.example.memorypractice.app.repeat.reqdto;

import com.example.memorypractice.app.repeat.RepeatType;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Set;

public record ReqRepeatEdit(RepeatType repeatType, Set<DayOfWeek> dayOfWeekSet, LocalDate startDate, LocalDate endDate,
                            Boolean clearEndDate) {
}
