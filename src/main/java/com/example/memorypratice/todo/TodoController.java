package com.example.memorypratice.todo;

import com.example.memorypratice.todo.reqdto.ReqWriteTodo;
import com.example.memorypratice.todo.resdto.ResTodo;
import com.example.memorypratice.todo.resdto.ResTodoList;
import com.example.memorypratice.todo.service.TodoR_Service;
import com.example.memorypratice.todo.service.TodoW_Service;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/todo")
public class TodoController {

    private final TodoR_Service r_service;
    private final TodoW_Service w_service;

    @PostMapping("/write")
    public ResponseEntity writeTodo(@AuthenticationPrincipal Long userId, ReqWriteTodo reqWriteTodo){

        return ResponseEntity.ok(w_service.writeTodo(userId, reqWriteTodo));
    }
    @DeleteMapping("/{todoId}")
    public ResponseEntity deleteTodo(@PathVariable("todoId") Long todoId,@AuthenticationPrincipal Long userId){
        w_service.deletTodo(userId,todoId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{todoId}")
    public ResponseEntity<ResTodo> readTodo(@PathVariable("todoId")Long todoId, @AuthenticationPrincipal Long userId){

        return ResponseEntity.ok(r_service.readTodo(userId, todoId));
    }

    @Validated
    @GetMapping("/list")
    public ResponseEntity<ResTodoList> readTodoList(@AuthenticationPrincipal Long userId,
                                                    @RequestParam(defaultValue = "0")@Positive int size,
                                                    @RequestParam(defaultValue = "10")@Positive int page){
        return ResponseEntity.ok(r_service.readTodoList(userId, page, size));
    }


}
