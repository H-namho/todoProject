package com.example.memorypractice.app.todo;

import com.example.memorypractice.app.todo.resdto.TodayTodo;
import com.example.memorypractice.app.todo.service.SseSerivce;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class TodoNoticeScheduler {

    private final SseSerivce sseSerivce;
    private final TodoRepository todoRepository;


    // 매일 아침 9시에 TodayTodo 알람
    @Scheduled(cron = "0 0 9 * * *", zone = "Asia/Seoul")
    @Transactional(readOnly = true)
    public void todayNotice(){

        LocalDate now = LocalDate.now();
        Map<Long,List<String>> resTodos = new HashMap<>();

        List<TodayTodo> todayTodo = todoRepository.findTodayTodo(now,false);
        for(TodayTodo t : todayTodo){
            if (!resTodos.containsKey(t.userId())) {
                resTodos.put(t.userId(), new ArrayList<>());
            }
            resTodos.get(t.userId()).add(t.title());
        }
        for(Long userId : resTodos.keySet()){
            sseSerivce.send(userId, "today-todo", resTodos.get(userId));
        }

    }
}
