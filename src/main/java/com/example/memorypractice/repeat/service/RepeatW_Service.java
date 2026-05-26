package com.example.memorypractice.repeat.service;

import com.example.memorypractice.repeat.*;
import com.example.memorypractice.repeat.reqdto.ReqRepeatEdit;
import com.example.memorypractice.repeat.reqdto.ReqRepeatTodo;
import com.example.memorypractice.user.UserEntity;
import com.example.memorypractice.user.UserRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class RepeatW_Service {

    private final RepeatTodoRepository repeatTodoRepository;
    private final RepeatCompleteRepository completeRepository;
    private final UserRepository userRepository;

    // 루틴 작성
    @Transactional
    public Long repeatWrite(ReqRepeatTodo reqRepeatTodo,Long userId){

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
        return repeatTodoRepository.save(repeatTodo).getId();
    }
    // 루틴 활성화 or 비활성화
    @Transactional
    public void changeActive(Long userId,Long repeatId) {

        RepeatTodoEntity repeatTodo = repeatTodoRepository.findByIdAndUser_Id(repeatId,userId)
                .orElseThrow(()-> new NoSuchElementException("해당 항목을 찾을 수 없습니다."));
        if(repeatTodo.isActive()){
            repeatTodo.deactivate();
        }else {
            repeatTodo.activate();
        }
    }
    // 루틴완료
    @Transactional
    public void completeRepeat(Long userId, Long repeatId) {

        RepeatTodoEntity repeatTodo = repeatTodoRepository.findByIdAndUser_Id(repeatId,userId)
                .orElseThrow(()-> new NoSuchElementException(("존재하지 않는 항목입니다")));
        LocalDate today = LocalDate.now();
        if(!repeatTodo.chkComplete(today)){
            throw new IllegalArgumentException("오늘 할 일이 아닙니다.");
        }
        if (completeRepository.existsByRepeatTodo_IdAndCompletedDate(repeatId, today)) {
            return;
        }

        RepeatTodoCompletionEntity completionEntity = new RepeatTodoCompletionEntity(repeatTodo,
                repeatTodo.getUser(),today);
        completeRepository.save(completionEntity);
    }
    // 루틴삭제
    @Transactional
    public void delRepeat(Long userId, Long repeatId) {
       RepeatTodoEntity repeatTodo =repeatTodoRepository.findByIdAndUser_Id(repeatId, userId)
                .orElseThrow(()-> new NoSuchElementException("존재하지 않는 항목입니다."));
       completeRepository.deleteByRepeatTodo_Id(repeatTodo.getId());
       repeatTodoRepository.delete(repeatTodo);
    }
    // 루틴 완료해제
    @Transactional
    public void unCompleteRepeat(Long userId, Long repeatId) {

        RepeatTodoEntity repeatTodo = repeatTodoRepository.findByIdAndUser_Id(repeatId, userId)
                .orElseThrow(() -> new NoSuchElementException("존재하지 않는 항목입니다."));
        LocalDate today = LocalDate.now();

        completeRepository.deleteByRepeatTodo_IdAndCompletedDate(repeatId,today);
    }
    // 루틴수정
    @Transactional
    public void editRepeat(Long userId, Long repeatId,ReqRepeatEdit repeatEdit) {

        RepeatTodoEntity repeatTodo = repeatTodoRepository.findByIdAndUser_Id(repeatId, userId)
                .orElseThrow(() -> new NoSuchElementException("존재하지 않는 항목입니다."));
        repeatTodo.update(repeatEdit.repeatType(), repeatEdit.dayOfWeekSet(), repeatEdit.startDate(),
                repeatEdit.endDate(), repeatEdit.clearEndDate());

    }
}
