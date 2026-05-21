package com.example.memorypratice.user.service;

import com.example.memorypratice.user.reqdto.ReqPassword;
import com.example.memorypratice.user.reqdto.ReqSignUp;
import com.example.memorypratice.user.reqdto.ReqNickname;
import com.example.memorypratice.user.resdto.ResProfile;
import com.example.memorypratice.user.UserEntity;
import com.example.memorypratice.user.UserRepository;
import com.example.memorypratice.user.UserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class UserW_Service {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // 회원가입
    @Transactional
    public void signUp(ReqSignUp reqSignUp){

        if(userRepository.existsByUsername(reqSignUp.username())){
            throw new IllegalArgumentException("이미 사용중인 아이디입니다");
        }
        String password = passwordEncoder.encode(reqSignUp.password());
        UserEntity user = new UserEntity(reqSignUp.username(), password, reqSignUp.nickname(), UserRole.USER);
        userRepository.save(user);
    }

    // 닉네임 변경
    @CacheEvict(cacheNames = "userProfile", key = "#userId")
    @Transactional
    public void updateNickname(ReqNickname reqUpdate, Long userId){

        UserEntity user =userRepository.findById(userId)
                .orElseThrow(()-> new UsernameNotFoundException("회원 정보가 존재하지 않습니다."));
        user.updateProfile(reqUpdate.nickname());
    }

    // 비밀번호 변경
    @Transactional
    public void updatePassword(ReqPassword reqPassword,Long userId){
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(()-> new UsernameNotFoundException("회원 정보가 존재하지 않습니다."));
        if (!passwordEncoder.matches(reqPassword.nowPassword(), user.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }
        String newPassword = passwordEncoder.encode(reqPassword.newPassword());
        user.changePassword(newPassword);
    }
}
