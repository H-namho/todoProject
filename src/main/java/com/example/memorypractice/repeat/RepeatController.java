package com.example.memorypractice.repeat;

import com.example.memorypractice.repeat.reqdto.ReqRepeatTodo;
import com.example.memorypractice.repeat.resdto.ResRepeatList;
import com.example.memorypractice.repeat.service.RepeatR_Service;
import com.example.memorypractice.repeat.service.RepeatW_Service;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/repeat")
@RequiredArgsConstructor
public class RepeatController {

    private final RepeatW_Service repeatW_service;
    private final RepeatR_Service repeatR_service;

    @GetMapping("/list")
    public ResponseEntity<List<ResRepeatList>> getAllRepeatList(@AuthenticationPrincipal Long userId){
        return ResponseEntity.ok(repeatR_service.readRepeat(userId));
    }
    @PostMapping("/write")
    public ResponseEntity<?> writeRepeat(@AuthenticationPrincipal Long userId,
                                      @RequestBody @Valid ReqRepeatTodo reqRepeatTodo){
        repeatW_service.repeatWrite(reqRepeatTodo, userId);
        return ResponseEntity.noContent().build();
    }
    @PatchMapping("/{repeatId}/active")
    public ResponseEntity<?> changeActive(@AuthenticationPrincipal Long userId,
                                       @PathVariable Long repeatId){
        repeatW_service.changeActive(userId,repeatId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{repeatId}/completed")
    public ResponseEntity<?> completeRepeat(@PathVariable Long repeatId,
                                      @AuthenticationPrincipal Long userId){
        repeatW_service.completeRepeat(userId,repeatId);
        return ResponseEntity.noContent().build();
    }

}
