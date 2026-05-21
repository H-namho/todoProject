package com.example.memorypratice.user;

import com.example.memorypratice.user.ReqDto.ReqSignUp;
import com.example.memorypratice.user.UserService.W_Service;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
public class UserController {

    private final W_Service wService;

    public ResponseEntity signUp(@Valid ReqSignUp reqSignUp){
        wService.SignUp(reqSignUp);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}