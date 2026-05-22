package com.example.memorypractice.todo;

import com.example.memorypractice.todo.reqdto.ReqRepeatTodo;
import com.example.memorypractice.todo.service.RepeatW_Service;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/repeat")
@RequiredArgsConstructor
public class RepeatController {

    private final RepeatW_Service repeatW_service;

    @PostMapping("/write")
    public ResponseEntity writeRepeat(@AuthenticationPrincipal Long userId,
                                      @RequestBody @Valid ReqRepeatTodo reqRepeatTodo){
        repeatW_service.repeatWrite(reqRepeatTodo, userId);
        return ResponseEntity.noContent().build();
    }
}
