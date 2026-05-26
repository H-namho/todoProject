package com.example.memorypractice.todo;

import com.example.memorypractice.todo.reqdto.ReqDeleteIds;
import com.example.memorypractice.todo.reqdto.ReqUpdateTodo;
import com.example.memorypractice.todo.reqdto.ReqWriteTodo;
import com.example.memorypractice.todo.resdto.ResTodo;
import com.example.memorypractice.todo.resdto.ResTodoList;
import com.example.memorypractice.todo.service.TodoR_Service;
import com.example.memorypractice.todo.service.TodoW_Service;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/todo")
public class TodoController {

    private final TodoR_Service r_service;
    private final TodoW_Service w_service;

    // 투두 작성
    @PostMapping("/write")
    public ResponseEntity<?> writeTodo(@AuthenticationPrincipal Long userId
                                      ,@Valid @RequestBody ReqWriteTodo reqWriteTodo){
        return ResponseEntity.status(HttpStatus.CREATED).body(w_service.writeTodo(userId, reqWriteTodo));
    }

    // 상세조회
    @GetMapping("/{todoId}")
    public ResponseEntity<ResTodo> readTodo(@PathVariable("todoId")Long todoId
            , @AuthenticationPrincipal Long userId){

        return ResponseEntity.ok(r_service.readTodo(userId, todoId));
    }

    // 목록조회
    @Validated
    @GetMapping("/list")
    public ResponseEntity<ResTodoList> readTodoList(@AuthenticationPrincipal Long userId,
                                                    @RequestParam(required = false) Boolean completed,
                                                    @RequestParam(required = false) TodoPriority priority,
                                                    @RequestParam(required = false) String keyword,
                                                    @RequestParam(defaultValue = "10")@Positive int size,
                                                    @RequestParam(defaultValue = "0")@PositiveOrZero int page){
        return ResponseEntity.ok(r_service.readTodoList(userId,completed,priority,keyword,page,size));
    }

    // 투두내용 수정
    @PatchMapping("/{todoId}/edit")
    public ResponseEntity todoEdit(@AuthenticationPrincipal Long userId,
                                   @PathVariable("todoId")Long todoId,
                                   @Valid @RequestBody ReqUpdateTodo reqUpdateTodo){
        w_service.updateTodo(userId,todoId,reqUpdateTodo);
        return ResponseEntity.ok().build();
    }

    // 완료여부 체크
    @PatchMapping("/{todoId}/chk")
    public ResponseEntity todoChk(@AuthenticationPrincipal Long userId,
                                  @PathVariable("todoId") Long todoId){
        w_service.completeTodo(userId,todoId);
        return ResponseEntity.noContent().build();
    }

    // 여러건삭제
    @DeleteMapping("/delete/todos")
    public ResponseEntity deleteTodos(@Valid @RequestBody ReqDeleteIds deleteIds,
                                      @AuthenticationPrincipal Long userId){
        w_service.deleteTodos(userId,deleteIds.todoIds());
        return ResponseEntity.noContent().build();
    }

    // 단건삭제
    @DeleteMapping("/{todoId}")
    public ResponseEntity<?> deleteTodo(@PathVariable("todoId") Long todoId
            ,@AuthenticationPrincipal Long userId){
        w_service.deleteTodo(userId,todoId);
        return ResponseEntity.noContent().build();
    }








}
