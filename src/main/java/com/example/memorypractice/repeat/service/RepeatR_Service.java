package com.example.memorypractice.repeat.service;

import com.example.memorypractice.repeat.RepeatTodoEntity;
import com.example.memorypractice.repeat.RepeatTodoRepository;
import com.example.memorypractice.repeat.resdto.ResRepeatList;
import com.example.memorypractice.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RepeatR_Service {

    private final RepeatTodoRepository repeatRepository;
    private final UserRepository userRepository;

    public List<ResRepeatList> readRepeat(Long userId){

        List<RepeatTodoEntity> repeatTodos = repeatRepository.findAllWithDayOfWeek(userId);
        if(repeatTodos.isEmpty()){
            return List.of();
        }
        List<ResRepeatList> resRepeatList = new ArrayList<>();
        for(RepeatTodoEntity r : repeatTodos){
            ResRepeatList repeat = new ResRepeatList(
                    r.getId(), r.getTitle(),r.getMemo(),r.getRepeatType()
                    ,r.getDayOfWeek(),r.isActive(),r.getStartDate(),r.getEndDate()
            );
            resRepeatList.add(repeat);
        }
        return resRepeatList;
    }
}
