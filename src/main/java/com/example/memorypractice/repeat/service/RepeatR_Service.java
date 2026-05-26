package com.example.memorypractice.repeat.service;

import com.example.memorypractice.repeat.RepeatCompleteRepository;
import com.example.memorypractice.repeat.RepeatTodoEntity;
import com.example.memorypractice.repeat.RepeatTodoRepository;
import com.example.memorypractice.repeat.RepeatType;
import com.example.memorypractice.repeat.resdto.ResCompleteDay;
import com.example.memorypractice.repeat.resdto.ResRepeatList;
import com.example.memorypractice.repeat.resdto.ResRepeatMonthlyCount;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RepeatR_Service {

    private final RepeatTodoRepository repeatRepository;
    private final RepeatCompleteRepository completeRepository;

    // 루틴 목록 보기
    public List<ResRepeatList> readRepeat(Long userId) {
        List<RepeatTodoEntity> repeatTodos = repeatRepository.findAllWithDayOfWeek(userId);
        if (repeatTodos.isEmpty()) {
            return List.of();
        }

        Set<Long> completedRepeatIds = completeRepository.findCompletedRepeatTodoIds(userId, LocalDate.now());
        List<ResRepeatList> resRepeatList = new ArrayList<>();
        for (RepeatTodoEntity repeatTodo : repeatTodos) {
            ResRepeatList repeat = new ResRepeatList(
                    repeatTodo.getId(),
                    repeatTodo.getTitle(),
                    repeatTodo.getMemo(),
                    repeatTodo.getRepeatType(),
                    repeatTodo.getDayOfWeek(),
                    repeatTodo.isActive(),
                    repeatTodo.getStartDate(),
                    repeatTodo.getEndDate(),
                    completedRepeatIds.contains(repeatTodo.getId())
            );
            resRepeatList.add(repeat);
        }
        return resRepeatList;
    }

    // 루틴 완료날짜 보기
    public List<ResCompleteDay> completeDay(Long userId, Long repeatId) {
        RepeatTodoEntity repeatTodo = getUserRepeatTodo(userId, repeatId);
        return completeRepository.findCompleteDaysByRepeatTodoId(repeatTodo.getId());
    }

    // 수행한 횟수 / 해당 월의 총 루틴횟수
    public ResRepeatMonthlyCount getCount(Long repeatId, Long userId, int month, int year) {
        RepeatTodoEntity repeatTodo = getUserRepeatTodo(userId, repeatId);

        YearMonth yearMonth = YearMonth.of(year, month);
        // 조회 월의 첫날
        LocalDate monthStart = yearMonth.atDay(1);
        // 조회 월의 마지막날
        LocalDate monthEnd = yearMonth.atEndOfMonth();

        // 조회 월의 첫날보다 루틴 시작일이 늦으면, 실제 루틴 시작일부터 횟수를 센다.
        // ex) 5월 조회, 루틴 시작일 5월 10일이면 5월 10일부터 계산한다.
        LocalDate countStart = monthStart;
        if (repeatTodo.getStartDate().isAfter(countStart)) {
            countStart = repeatTodo.getStartDate();
        }

        // 조회 월의 마지막 날보다 루틴 종료일이 빠르면, 루틴 종료일까지만 횟수를 센다.
        // 종료일이 null이면 기간 제한이 없으므로 조회 월의 마지막 날까지 계산한다.
        LocalDate countEnd = monthEnd;
        if (repeatTodo.getEndDate() != null && repeatTodo.getEndDate().isBefore(countEnd)) {
            countEnd = repeatTodo.getEndDate();
        }


        int totalCount = 0;

        if (!countStart.isAfter(countEnd)) {

            for (LocalDate date = countStart; !date.isAfter(countEnd); date = date.plusDays(1)) {
                // DAILY -> 매일 ++
                if (repeatTodo.getRepeatType() == RepeatType.DAILY) {
                   totalCount++;
                }

                // WEEKLY이고 요일이 같은값 -> 해당요일을 포함할때만 ++
                if (repeatTodo.getRepeatType() == RepeatType.WEEKLY
                        && repeatTodo.getDayOfWeek().contains(date.getDayOfWeek())) {
                    totalCount++;
                }
            }
        }
        // 완료 횟수
        long completedCount = completeRepository.countByRepeatTodo_IdAndCompletedDateBetween(
                repeatTodo.getId(), monthStart, monthEnd);

        return new ResRepeatMonthlyCount(repeatTodo.getId(), year, month, totalCount, completedCount);
    }

    private RepeatTodoEntity getUserRepeatTodo(Long userId, Long repeatId) {
        Optional<RepeatTodoEntity> optionalRepeatTodo = repeatRepository.findByIdAndUser_Id(repeatId, userId);
        if (optionalRepeatTodo.isEmpty()) {
            throw new NoSuchElementException("존재하지 않는 항목입니다.");
        }
        return optionalRepeatTodo.get();
    }
}
