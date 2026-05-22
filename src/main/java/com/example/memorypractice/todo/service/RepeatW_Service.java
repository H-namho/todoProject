package com.example.memorypractice.todo.service;

import com.example.memorypractice.todo.RepeatCompleteRepository;
import com.example.memorypractice.todo.RepeatTodoEntity;
import com.example.memorypractice.todo.RepeatTodoRepository;
import com.example.memorypractice.todo.RepeatType;
import com.example.memorypractice.todo.reqdto.ReqRepeatTodo;
import com.example.memorypractice.user.UserEntity;
import com.example.memorypractice.user.UserRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RepeatW_Service {

    private final RepeatTodoRepository repeatTodoRepository;
    private final RepeatCompleteRepository completeRepository;
    private final UserRepository userRepository;

    @Transactional
    public void repeatWrite(ReqRepeatTodo reqRepeatTodo,Long userId){

        UserEntity user = userRepository.findById(userId)
                .orElseThrow(()-> new UsernameNotFoundException("해당유저를 찾을 수 없습니다"));
        if(reqRepeatTodo.repeatType()== RepeatType.DAILY && reqRepeatTodo.dayOfWeek()!=null && !reqRepeatTodo.dayOfWeek().isEmpty()){
            throw new IllegalArgumentException("매일 반복은 요일을 지정할 수 없습니다.");
        }
        if(reqRepeatTodo.repeatType()== RepeatType.WEEKLY && (reqRepeatTodo.dayOfWeek()==null || reqRepeatTodo.dayOfWeek().isEmpty())){
            throw new IllegalArgumentException("매주 반복할 요일을 지정해주세요.");
        }

        if(reqRepeatTodo.endDate() != null && reqRepeatTodo.endDate().isBefore(reqRepeatTodo.startDate())){
            throw new IllegalArgumentException("종료일이 시작일보다 빠릅니다.");
        }
        RepeatTodoEntity repeatTodo = new RepeatTodoEntity(user,reqRepeatTodo.title(),reqRepeatTodo.memo(),reqRepeatTodo.repeatType(),
                                                        reqRepeatTodo.dayOfWeek(),reqRepeatTodo.startDate(),reqRepeatTodo.endDate());
        repeatTodoRepository.save(repeatTodo);
    }
}
