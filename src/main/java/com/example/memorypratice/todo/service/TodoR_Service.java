package com.example.memorypratice.todo.service;

import com.example.memorypratice.todo.TodoEntity;
import com.example.memorypratice.todo.TodoPriority;
import com.example.memorypratice.todo.TodoRepository;
import com.example.memorypratice.todo.resdto.ResTodo;
import com.example.memorypratice.todo.resdto.ResTodoList;
import com.example.memorypratice.user.UserEntity;
import com.example.memorypratice.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TodoR_Service {

    private final TodoRepository todoRepository;
    private final UserRepository userRepository;

    // 단건 조회
    @Cacheable(cacheNames = "todo", key = "#userId + ':' + #todoId")
    public ResTodo readTodo(Long userId,Long todoId){

        if(!userRepository.existsById(userId)){
            throw new UsernameNotFoundException("회원정보를 찾을 수 없습니다.");
        }

        TodoEntity todo = todoRepository.findByIdAndUser_Id(todoId,userId)
                .orElseThrow(()-> new NoSuchElementException("해당 todo를 찾을 수 없습니다"));
        return new ResTodo(todo.getId(),todo.getTitle(), todo.getMemo(),
                todo.isCompleted(),todo.getPriority(),todo.getCreatedAt(),todo.getDueDate());
    }

    // 목록 조회
    @Cacheable(cacheNames = "todoList", key = "#userId + ':' + #page + ':' + #size")
    public ResTodoList readTodoList(Long userId, int page, int size){

        UserEntity user = userRepository.findById(userId)
                .orElseThrow(()-> new UsernameNotFoundException("회원정보를 찾을 수 없습니다."));
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<TodoEntity> todoList = todoRepository.findByUser_Id(userId,pageable);

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
