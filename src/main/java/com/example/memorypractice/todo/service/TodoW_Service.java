package com.example.memorypractice.todo.service;

import com.example.memorypractice.todo.TodoEntity;
import com.example.memorypractice.todo.TodoRepository;
import com.example.memorypractice.repeat.reqdto.ReqUpdateTodo;
import com.example.memorypractice.repeat.reqdto.ReqWriteTodo;
import com.example.memorypractice.user.UserEntity;
import com.example.memorypractice.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;

import org.springframework.cache.annotation.Caching;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class TodoW_Service {

    private final TodoRepository todoRepository;
    private final UserRepository userRepository;

    // 투두 생성
    @CacheEvict(cacheNames = "todoList", allEntries = true)
    @Transactional
    public Long writeTodo(Long userId, ReqWriteTodo writeTodo){

        UserEntity user = userRepository.findById(userId)
                .orElseThrow(()-> new UsernameNotFoundException("회원정보를 찾을 수 없습니다."));
        TodoEntity todo = new TodoEntity(user,writeTodo.title(), writeTodo.memo()
                , writeTodo.dueDate(), writeTodo.todoPriority());
        TodoEntity savedTodo = todoRepository.save(todo);
        return savedTodo.getId();
    }

    // 투두 수정
    // 추후 캐시 삭제방법 수정
    @Caching(evict = {
            @CacheEvict(cacheNames = "todo", key = "#userId + ':' + #todoId"),
            @CacheEvict(cacheNames = "todoList", allEntries = true)
    })
    @Transactional
    public void updateTodo(Long userId,Long todoId,ReqUpdateTodo reqUpdateTodo){

//        UserEntity user = userRepository.findById(userId)
//                .orElseThrow(()-> new UsernameNotFoundException("회원정보를 찾을 수 없습니다."));
        TodoEntity todo = todoRepository.findByIdAndUser_Id(todoId,userId)
                .orElseThrow(()-> new NoSuchElementException("해당 todo를 찾을 수 없습니다"));
        todo.update(reqUpdateTodo.title(),reqUpdateTodo.memo(),reqUpdateTodo.dueDate(),reqUpdateTodo.priority());
    }

    // 투두 완료
    // 추후 캐시 삭제방법 수정
    @Caching(evict = {
            @CacheEvict(cacheNames = "todo", key = "#userId + ':' + #todoId"),
            @CacheEvict(cacheNames = "todoList", allEntries = true)
    })
    @Transactional
    public void completeTodo(Long userId, Long todoId){

        TodoEntity todo = todoRepository.findByIdAndUser_Id(todoId,userId)
                .orElseThrow(()-> new NoSuchElementException("해당 todo를 찾을 수 없습니다"));

        if (todo.isCompleted()) {
            todo.reopen();
        } else {
            todo.complete();
        }

    }

    // 투두 삭제
    // 추후 캐시 삭제방법 수정
    @Caching(evict = {
            @CacheEvict(cacheNames = "todo", key = "#userId + ':' + #todoId"),
            @CacheEvict(cacheNames = "todoList", allEntries = true)
    })
    @Transactional
    public void deleteTodo(Long userId, Long todoId){

//        UserEntity user = userRepository.findById(userId)
//                .orElseThrow(()-> new UsernameNotFoundException("회원정보를 찾을 수 없습니다."));
        TodoEntity todo = todoRepository.findByIdAndUser_Id(todoId,userId)
                .orElseThrow(()-> new NoSuchElementException("해당 todo를 찾을 수 없습니다"));
        todoRepository.delete(todo);
    }
    @Caching(evict = {
            @CacheEvict(cacheNames = "todo",allEntries = true),
            @CacheEvict(cacheNames = "todoList", allEntries = true)
    })
    @Transactional
    public void deleteTodos(Long userId, List<Long> todoIds) {
        todoRepository.deleteTodoIds(userId,todoIds);
    }
}
