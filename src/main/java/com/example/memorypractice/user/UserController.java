package com.example.memorypractice.user;

import com.example.memorypractice.user.reqdto.*;
import com.example.memorypractice.user.resdto.ResLogin;
import com.example.memorypractice.user.resdto.ResProfile;
import com.example.memorypractice.user.service.UserR_Service;
import com.example.memorypractice.user.service.UserW_Service;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
public class UserController {

    private final UserW_Service wService;
    private final UserR_Service rService;

    // 회원가입
    @PostMapping("/signup")
    public ResponseEntity<?> signUp(@Valid @RequestBody ReqSignUp reqSignUp){
        wService.signUp(reqSignUp);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
    // 로그인
    @PostMapping("/signin")
    public ResponseEntity<ResLogin> signIn(@Valid @RequestBody ReqLogin reqLogin){
        return ResponseEntity.ok(rService.login(reqLogin));
    }
    // 닉네임수정
    @PatchMapping("/edit")
    public ResponseEntity<?> editUser(@Valid @RequestBody ReqNickname reqNickname
                                ,@AuthenticationPrincipal Long userId){
        wService.updateNickname(reqNickname,userId);
        return ResponseEntity.ok().build();
    }
    // 비밀번호 변경
    @PatchMapping("/editPw")
    public ResponseEntity<?> editPw(@Valid @RequestBody ReqPassword reqPassword,
                                 @AuthenticationPrincipal Long userId){
        wService.updatePassword(reqPassword,userId);
        return ResponseEntity.ok().build();
    }
    // 내정보
    @GetMapping("/getinfo")
    public ResponseEntity<ResProfile> getUserInfo(@AuthenticationPrincipal Long userId){
        return ResponseEntity.ok(rService.getProfile(userId));
    }
    // 리프레시토큰 재발급
    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@RequestBody @Valid ReqRereshToken reqRereshToken){
        return ResponseEntity.ok(rService.refresh(reqRereshToken.refreshToken()));
    }
    // 로그아웃
    @PostMapping("/logout")
    public ResponseEntity<?> logout(@AuthenticationPrincipal Long userId,
                                    @RequestHeader("Authorization")String authHeader){
        if(!authHeader.startsWith("Bearer ")){
            throw new IllegalArgumentException("유효하지 않은 헤더입니다.");
        }
        String accessToken = authHeader.substring("Bearer ".length());
        rService.logout(userId,accessToken);
        return ResponseEntity.noContent().build();
    }
}