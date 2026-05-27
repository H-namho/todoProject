package com.example.memorypractice.app.repeat.resdto;

import com.example.memorypractice.app.repeat.RepeatType;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Set;

public record ResRepeatList(Long repeatId, String title, String memo,
                            RepeatType repeatType, Set<DayOfWeek> dayOfWeek, boolean active,
                            LocalDate startDate, LocalDate endDate, boolean completedToday) {
}
