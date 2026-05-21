package com.example.memorypratice.user.UserService;

import com.example.memorypratice.user.ReqDto.ReqPassword;
import com.example.memorypratice.user.ReqDto.ReqSignUp;
import com.example.memorypratice.user.ReqDto.ReqNickname;
import com.example.memorypratice.user.ResDto.ResProfile;
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
public class W_Service {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public void SignUp(ReqSignUp reqSignUp){

        if(userRepository.existsByUsername(reqSignUp.username())){
            throw new IllegalArgumentException("이미 사용중인 아이디입니다");
        }
        String password = passwordEncoder.encode(reqSignUp.password());
        UserEntity user = new UserEntity(reqSignUp.username(), password, reqSignUp.nickname(), UserRole.USER);
        userRepository.save(user);
    }

    @CacheEvict(cacheNames = "userProfile", key = "#userId")
    @Transactional
    public ResProfile updateNickname(ReqNickname reqUpdate, Long userId){

        UserEntity user =userRepository.findById(userId)
                .orElseThrow(()-> new UsernameNotFoundException("회원 정보가 존재하지 않습니다."));
        user.updateProfile(reqUpdate.nickname());
        return new ResProfile(user.getId(), user.getUsername(),user.getNickname());
    }

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
