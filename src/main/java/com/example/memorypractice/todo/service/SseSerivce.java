package com.example.memorypractice.todo.service;

import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

@Service
public class SseSerivce {

    // 한 사용자가 여러 브라우저 탭을 열 수 있으므로 userId마다 여러 emitter를 유지
    private final Map<Long, List<SseEmitter>> emitters = new ConcurrentHashMap<>();

    public SseEmitter subscribe(Long userId) {

        SseEmitter sseEmitter = new SseEmitter(30* 60 * 1000L);
        // 기존 키 있는지 검사
        if (!emitters.containsKey(userId)) {
            // emitters에 없다면 만들어라
            emitters.putIfAbsent(userId, new CopyOnWriteArrayList<>());
        }
        // emitters에 key : userId , value : [sseEmitter] 추가
        emitters.get(userId).add(sseEmitter);

        // 연결이 끝난 emitter만 제거해서 같은 사용자의 다른 브라우저 연결은 유지한다.
        sseEmitter.onCompletion(new Runnable() {
            @Override
            public void run() {
                removeEmitter(userId, sseEmitter);
            }
        });
        sseEmitter.onTimeout(new Runnable() {
            @Override
            public void run() {
                removeEmitter(userId, sseEmitter);
            }
        });
        sseEmitter.onError(new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) {
                removeEmitter(userId, sseEmitter);
            }
        });

        sendToEmitter(userId, sseEmitter, "connect", "SSE 연결 완료");
        return sseEmitter;
    }

    public void send(Long userId, String eventName, Object data) {

        // 하나의 userId에 들어있는 SseEmitter 리스트
        List<SseEmitter> userEmitters = emitters.get(userId);
        if (userEmitters == null || userEmitters.isEmpty()) {
            return;
        }
        for (SseEmitter sseEmitter : userEmitters) {
            sendToEmitter(userId, sseEmitter, eventName, data);
        }

    }

    private boolean sendToEmitter(Long userId, SseEmitter sseEmitter, String eventName, Object data) {
        try {
            sseEmitter.send(SseEmitter.event()
                    .name(eventName)
                    .data(data));
            return true;
        } catch (IOException e) {
            removeEmitter(userId, sseEmitter);
            return false;
        }
    }

    private void removeEmitter(Long userId, SseEmitter sseEmitter) {
        List<SseEmitter> userEmitters = emitters.get(userId);
        if (userEmitters == null) {
            return;
        }
        userEmitters.remove(sseEmitter);
        if (userEmitters.isEmpty()) {
            emitters.remove(userId);
        }
    }
}
