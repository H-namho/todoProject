package com.example.memorypratice.user;

import com.example.memorypratice.user.reqdto.ReqSignUp;
import com.example.memorypratice.user.service.UserW_Service;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
public class UserController {

    private final UserW_Service wService;

    public ResponseEntity signUp(@Valid @RequestBody ReqSignUp reqSignUp){
        wService.signUp(reqSignUp);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}