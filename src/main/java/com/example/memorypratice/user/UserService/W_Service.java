package com.example.memorypratice.user.UserService;

import com.example.memorypratice.user.ReqDto.ReqSignUp;
import com.example.memorypratice.user.UserEntity;
import com.example.memorypratice.user.UserRepository;
import com.example.memorypratice.user.UserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class W_Service {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public void SignUp(ReqSignUp reqSignUp){

        if(userRepository.existsByUsername(reqSignUp.username())){
            throw new IllegalArgumentException("이미 사용중인 아이디입니다");
        }
        String password = passwordEncoder.encode(reqSignUp.password());
        UserEntity user = new UserEntity(reqSignUp.username(), password, reqSignUp.nickname(), UserRole.USER);
        userRepository.save(user);
    }
}
