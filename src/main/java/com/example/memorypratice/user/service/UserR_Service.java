package com.example.memorypratice.user.service;

import com.example.memorypratice.jwt.JwtProvider;
import com.example.memorypratice.user.reqdto.ReqLogin;
import com.example.memorypratice.user.resdto.ResLogin;
import com.example.memorypratice.user.resdto.ResProfile;
import com.example.memorypratice.user.UserEntity;
import com.example.memorypratice.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserR_Service {

    private final JwtProvider jwtProvider;
    private final UserRepository userRepository;
    private final PasswordEncoder encoder;

    // 로그인
    public ResLogin login(ReqLogin reqLogin){
       UserEntity user =userRepository.findByUsername(reqLogin.username())
                .orElseThrow(()-> new UsernameNotFoundException("존재하지 않는 회원입니다."));
       if(!encoder.matches(reqLogin.password(), user.getPassword())){
           throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }
       String accessToken = jwtProvider.createAcToken(user.getId(), user.getUsername(), user.getRole().name());
       String refreshToken = jwtProvider.createReToekn(user.getId(), user.getUsername(), user.getRole().name());

       // redis를 활용해서 리프레시토큰 추가 예정
       return new ResLogin(accessToken, refreshToken);
    }

    // 프로필 조회
    @Cacheable(cacheNames = "userProfile", key = "#userId")
    public ResProfile getProfile(Long userId){
        UserEntity user = userRepository
                .findById(userId).orElseThrow(()-> new UsernameNotFoundException("회원 정보가 존재하지 않습니다"));
        return new ResProfile(user.getId(), user.getUsername(), user.getNickname());
    }
}
