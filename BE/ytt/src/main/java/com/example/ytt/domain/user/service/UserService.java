package com.example.ytt.domain.user.service;

import com.example.ytt.domain.user.auth.jwt.JWTUtil;
import com.example.ytt.domain.user.auth.security.CustomUserDetails;
import com.example.ytt.domain.user.domain.RefreshEntity;
import com.example.ytt.domain.user.domain.User;
import com.example.ytt.domain.user.dto.*;
import com.example.ytt.domain.user.exception.UserException;
import com.example.ytt.domain.user.exception.UserExceptionType;
import com.example.ytt.domain.user.repository.RefreshRepository;
import com.example.ytt.domain.user.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Slf4j
@Transactional
@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService{

    @Autowired
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final JWTUtil jwtUtil;
    private final RefreshRepository refreshRepository;

    @Value("${jwt.expiration.access}")
    private Long ACCESS_TOKEN_EXPIRATION;

    @Value("${jwt.expiration.refresh}")
    private Long REFRESH_TOKEN_EXPIRATION;



    // 회원가입
    public void signUp(SignUpDto signUpDto) throws UserException {

        signUpDto.setPassword(bCryptPasswordEncoder.encode(signUpDto.getPassword()));
        User user = signUpDto.toEntity(signUpDto);

        // 아이디 중복 검사
        if(userRepository.existsByEmail(signUpDto.getEmail())){
            throw new UserException(UserExceptionType.ALREADY_EXIST_USER);
        }

        // 폰번호 중복 검사
        if(userRepository.existsByPhoneNumber(signUpDto.getPhoneNumber())){
            throw new UserException(UserExceptionType.ALREADY_EXIST_PHONENUMBER);
        }

        userRepository.save(user);
    }

    // 토큰 재발급 요청 확인
    public TokenResponseDto reissueTokens(HttpServletRequest request, HttpServletResponse response) throws UserException{
        String refresh = request.getHeader("refresh");

        if (refresh == null) {
            throw new UserException(UserExceptionType.BLANK_REFRESH_TOKEN);
        }

        if (jwtUtil.isExpired(refresh)) {
            throw new UserException(UserExceptionType.EXPIRED_REFRESH_TOKEN);
        }

        String category = jwtUtil.getCategory(refresh);
        if (!category.equals("refresh")) {
            throw new UserException(UserExceptionType.INVALID_TOKEN_CATEGORY);
        }

        if (!refreshRepository.existsByRefresh(refresh)) {
            throw new UserException(UserExceptionType.INVALID_REFRESH_TOKEN);
        }

        return generateNewTokens(refresh, response);
    }

    // 토큰 재발급
    private TokenResponseDto generateNewTokens(String refresh, HttpServletResponse response) {
        String email = jwtUtil.getEmail(refresh);
        String roleString = jwtUtil.getRole(refresh); // 역할 문자열 가져오기
        Role role = Role.valueOf(roleString.toUpperCase());

        // 새로운 JWT 생성
        String newAccess = jwtUtil.createJwt("access", email, role, ACCESS_TOKEN_EXPIRATION);
        String newRefresh = jwtUtil.createJwt("refresh", email, role, REFRESH_TOKEN_EXPIRATION);

        // 기존 Refresh 토큰 삭제 후 새 Refresh 토큰 저장
        refreshRepository.deleteByRefresh(refresh);
        addRefreshEntity(email, newRefresh, REFRESH_TOKEN_EXPIRATION);

        // 응답 헤더에 새로운 토큰 설정
        response.setHeader("access", newAccess);
        response.setHeader("refresh", newRefresh);

        return new TokenResponseDto(newAccess, newRefresh);
    }


    // [로그인 실행 3] security DB 로그인 인증
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("해당 유저를 찾을 수 없습니다."));

        UserDto userDto = new UserDto(
                user.getId(),
                user.getEmail(),
                user.getPassword(),
                user.getName(),
                user.getPhoneNumber(),
                user.getRole()
        );

        return new CustomUserDetails(userDto);
    }

    // 서버에 저장되는 RefreshEntity 생성 메소드
    private void addRefreshEntity(String email, String refresh, Long expiredMs) {

        Date date = new Date(System.currentTimeMillis() + expiredMs);

        RefreshEntity refreshEntity = new RefreshEntity();
        refreshEntity.setEmail(email);
        refreshEntity.setRefresh(refresh);
        refreshEntity.setExpiration(date.toString());

        refreshRepository.save(refreshEntity);
    }
}
