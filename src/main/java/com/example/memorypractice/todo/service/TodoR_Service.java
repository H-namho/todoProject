package com.example.memorypractice.todo.service;

import com.example.memorypractice.todo.TodoEntity;
import com.example.memorypractice.todo.TodoPriority;
import com.example.memorypractice.todo.TodoRepository;
import com.example.memorypractice.todo.resdto.ResTodo;
import com.example.memorypractice.todo.resdto.ResTodoList;
import com.example.memorypractice.user.UserEntity;
import com.example.memorypractice.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
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
    @Cacheable(cacheNames = "todoList", key = "#userId + ':' + #completed + ':' + #priority + ':' + #keyword + ':' + #page + ':' + #size")
    public ResTodoList readTodoList(Long userId, Boolean completed, TodoPriority priority, String keyword, int page, int size){

//        UserEntity user = userRepository.findById(userId)
//                .orElseThrow(()-> new UsernameNotFoundException("로그인 후 조회가 가능합니다."));
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        String searchKeyword = keyword;
        if(searchKeyword != null && searchKeyword.isBlank()){
            searchKeyword = null;
        }
        // 조건많아지면 QueryDsl
        Page<TodoEntity> todoList = todoRepository.searchTodos(userId, completed, priority, searchKeyword, pageable);

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
        return new ResTodoList(todos,totalCount, completedCount, totalPages, hasNext);
    }


}
