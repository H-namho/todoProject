package com.example.memorypractice.repeat;

import com.example.memorypractice.repeat.reqdto.ReqRepeatEdit;
import com.example.memorypractice.repeat.reqdto.ReqRepeatTodo;
import com.example.memorypractice.repeat.resdto.ResCompleteDay;
import com.example.memorypractice.repeat.resdto.ResRepeatList;
import com.example.memorypractice.repeat.resdto.ResRepeatMonthlyCount;
import com.example.memorypractice.repeat.service.RepeatR_Service;
import com.example.memorypractice.repeat.service.RepeatW_Service;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/repeat")
@RequiredArgsConstructor
@Validated
public class RepeatController {

    private final RepeatW_Service repeatW_service;
    private final RepeatR_Service repeatR_service;

    // 루틴 리스트
    @GetMapping("/list")
    public ResponseEntity<List<ResRepeatList>> getAllRepeatList(@AuthenticationPrincipal Long userId){
        return ResponseEntity.ok(repeatR_service.readRepeat(userId));
    }
    // 루틴 작성
    @PostMapping("/write")
    public ResponseEntity<?> writeRepeat(@AuthenticationPrincipal Long userId,
                                      @RequestBody @Valid ReqRepeatTodo reqRepeatTodo){
        return ResponseEntity.ok(repeatW_service.repeatWrite(reqRepeatTodo, userId));
    }
    // 루틴 활성화
    @PatchMapping("/{repeatId}/active")
    public ResponseEntity<?> changeActive(@AuthenticationPrincipal Long userId,
                                       @PathVariable Long repeatId){
        repeatW_service.changeActive(userId,repeatId);
        return ResponseEntity.noContent().build();
    }
    // 루틴 완료
    @PatchMapping("/{repeatId}/completed")
    public ResponseEntity<?> completeRepeat(@PathVariable Long repeatId,
                                      @AuthenticationPrincipal Long userId){
        repeatW_service.completeRepeat(userId,repeatId);
        return ResponseEntity.noContent().build();
    }
    // 루틴완료 취소
    @DeleteMapping("/{repeatId}/completed")
    public ResponseEntity<?> uncompleteRepeat(@PathVariable Long repeatId,
                                              @AuthenticationPrincipal Long userId){
        repeatW_service.unCompleteRepeat(userId,repeatId);
        return ResponseEntity.noContent().build();
    }

    // 루틴 삭제
    @DeleteMapping("/{repeatId}")
    public ResponseEntity<?> delRepeat(@PathVariable Long repeatId,
                                       @AuthenticationPrincipal Long userId){
        repeatW_service.delRepeat(userId,repeatId);
        return ResponseEntity.noContent().build();
    }
    // 루틴 수정
    @PatchMapping("/{repeatId}")
    public ResponseEntity<?> editRepeat(@PathVariable Long repeatId, @AuthenticationPrincipal Long userId,
                                        @RequestBody ReqRepeatEdit repeatEdit){
        repeatW_service.editRepeat(userId,repeatId,repeatEdit);
        return ResponseEntity.ok().build();
    }
    // 루틴 한날 조회
    @GetMapping("/{repeatId}/day")
    public ResponseEntity<List<ResCompleteDay>> completedDay(@PathVariable Long repeatId
                                            ,@AuthenticationPrincipal Long userId){
        return ResponseEntity.ok(repeatR_service.completeDay(userId,repeatId));
    }

    //  수행한 횟수 / 그달의 총 횟수
    @GetMapping("/{repeatId}/allday")
    public ResponseEntity<ResRepeatMonthlyCount> getCount(@PathVariable Long repeatId,
                                                           @AuthenticationPrincipal Long userId,
                                                           @RequestParam @Min(1) @Max(12) int month,
                                                           @RequestParam int year){
        return ResponseEntity.ok(repeatR_service.getCount(repeatId, userId, month, year));
    }




}
