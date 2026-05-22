package com.example.memorypractice.filter;

import com.example.memorypractice.jwt.JwtProvider;
import com.example.memorypractice.redis.TokenRedisRepositry;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final JwtProvider jwtProvider;
    private final TokenRedisRepositry tokenRedisRepositry;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String header = request.getHeader("Authorization");
        if(header==null || !header.startsWith("Bearer ")){
            filterChain.doFilter(request,response);
            return;
        }
        String token = header.substring(7);
        if(token==null || !jwtProvider.vaildateToken(token)){
            filterChain.doFilter(request, response);
            return;
        };
        if(tokenRedisRepositry.hasBlackKey(token)){
            SecurityContextHolder.clearContext();
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("{\"message\":\"로그아웃된 토큰입니다.\"}");
            return;
        }
        Long userId = jwtProvider.getUserId(token);
        String role = jwtProvider.getRole(token);
        UsernamePasswordAuthenticationToken auth =
                new UsernamePasswordAuthenticationToken(userId, null, List.of(new SimpleGrantedAuthority("ROLE_"+role)));
        SecurityContextHolder.getContext().setAuthentication(auth);
        filterChain.doFilter(request,response);
    }
}
