package com.example.memorypractice.repeat.service;

import com.example.memorypractice.repeat.RepeatCompleteRepository;
import com.example.memorypractice.repeat.RepeatTodoEntity;
import com.example.memorypractice.repeat.RepeatTodoRepository;
import com.example.memorypractice.repeat.resdto.ResRepeatList;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class RepeatR_Service {

    private final RepeatTodoRepository repeatRepository;
    private final RepeatCompleteRepository completeRepository;

    @Transactional(readOnly = true)
    public List<ResRepeatList> readRepeat(Long userId){

        List<RepeatTodoEntity> repeatTodos = repeatRepository.findAllWithDayOfWeek(userId);
        if(repeatTodos.isEmpty()){
            return List.of();
        }
        Set<Long> completedRepeatIds = completeRepository.findCompletedRepeatTodoIds(userId, LocalDate.now());
        List<ResRepeatList> resRepeatList = new ArrayList<>();
        for(RepeatTodoEntity r : repeatTodos){
            ResRepeatList repeat = new ResRepeatList(
                    r.getId(), r.getTitle(),r.getMemo(),r.getRepeatType()
                    ,r.getDayOfWeek(),r.isActive(),r.getStartDate(),r.getEndDate(),
                    completedRepeatIds.contains(r.getId())
            );
            resRepeatList.add(repeat);
        }
        return resRepeatList;
    }
}
