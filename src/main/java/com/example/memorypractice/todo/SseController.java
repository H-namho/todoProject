package com.example.memorypractice.todo;

import com.example.memorypractice.todo.service.SseSerivce;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequestMapping("/api/notice")
@RequiredArgsConstructor
public class SseController {

    private final SseSerivce sseSerivce;

    // Sse구독 주소
    @GetMapping(value = "/subscribe", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter subScribe(@AuthenticationPrincipal Long userId){
       return sseSerivce.subscribe(userId);
    }

}
