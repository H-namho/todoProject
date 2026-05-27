package com.example.memorypractice.app.todo.service;

import com.example.memorypractice.app.todo.TodoEntity;
import com.example.memorypractice.app.todo.TodoPriority;
import com.example.memorypractice.app.todo.TodoRepository;
import com.example.memorypractice.app.todo.resdto.ResTodo;
import com.example.memorypractice.app.todo.resdto.ResTodoList;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TodoR_Service {

    private final TodoRepository todoRepository;

    // 단건 조회
    @Cacheable(cacheNames = "todo", key = "#userId + ':' + #todoId")
    public ResTodo readTodo(Long userId,Long todoId){

//        if(!userRepository.existsById(userId)){
//            throw new UsernameNotFoundException("회원정보를 찾을 수 없습니다.");
//        }
        TodoEntity todo = todoRepository.findByIdAndUser_Id(todoId,userId)
                .orElseThrow(()-> new NoSuchElementException("해당 todo를 찾을 수 없습니다"));
        return new ResTodo(todo.getId(),todo.getTitle(), todo.getMemo(),
                todo.isCompleted(),todo.getPriority(),todo.getCreatedAt(),todo.getDueDate());
    }

    // 목록 조회
    @Cacheable(cacheNames = "todoList", key = "#userId + ':' + #completed + ':' + #priority + ':' + #keyword + ':' + #dueDateFrom + ':' + #dueDateTo + ':' + #page + ':' + #size")
    public ResTodoList readTodoList(Long userId, Boolean completed, TodoPriority priority, String keyword,
                                    LocalDate dueDateFrom, LocalDate dueDateTo, int page, int size){

//        UserEntity user = userRepository.findById(userId)
//                .orElseThrow(()-> new UsernameNotFoundException("로그인 후 조회가 가능합니다."));
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        String searchKeyword = keyword;
        if(searchKeyword != null && searchKeyword.isBlank()){
            searchKeyword = null;
        }
        // 조건많아지면 QueryDsl
        Page<TodoEntity> todoList = todoRepository.searchTodos(
                userId, completed, priority, searchKeyword, dueDateFrom, dueDateTo, pageable);

        List<ResTodo> todos = new ArrayList<>();
        for(TodoEntity todo: todoList){
            ResTodo resTodo = new ResTodo(
                    todo.getId(),
                    todo.getTitle(),
                    todo.getMemo(),
                    todo.isCompleted(),
                    todo.getPriority(),
                    todo.getCreatedAt(),
                    todo.getDueDate()
            );
            todos.add(resTodo);
        }
        long totalCount = todoList.getTotalElements();
        long completedCount = todoRepository.countByUser_IdAndCompleted(userId,true);
        long totalPages = todoList.getTotalPages();
        boolean hasNext = todoList.hasNext();
        long todayCount = todoRepository.countByUser_IdAndDueDate(userId, LocalDate.now());
        long highCount = todoRepository.countByUser_IdAndPriorityAndCompletedFalse(userId, TodoPriority.HIGH);
        return new ResTodoList(todos,totalCount, completedCount, totalPages, hasNext, todayCount, highCount);
    }


}
