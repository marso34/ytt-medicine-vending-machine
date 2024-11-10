package com.example.ytt.domain.user.service;

import com.example.ytt.domain.user.domain.User;
import com.example.ytt.domain.user.dto.*;
import com.example.ytt.domain.user.exception.UserException;
import com.example.ytt.domain.user.repository.UserRepository;
import com.example.ytt.global.error.code.ExceptionType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Transactional
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final AuthService authService;


    // 회원가입
    public void signUp(SignUpDto signUpDto) throws UserException {

        signUpDto.setPassword(bCryptPasswordEncoder.encode(signUpDto.getPassword()));
        User user = User.builder()
                .email(signUpDto.getEmail())
                .password(signUpDto.getPassword())
                .name(signUpDto.getName())
                .phoneNumber(signUpDto.getPhoneNumber())
                .role(signUpDto.getRole())
                .build();

        // 아이디 중복 검사
        if(userRepository.existsByEmail(signUpDto.getEmail())){
            throw new UserException(ExceptionType.ALREADY_EXIST_USER);
        }

        // 폰번호 중복 검사
        if(userRepository.existsByPhoneNumber(signUpDto.getPhoneNumber())){
            throw new UserException(ExceptionType.ALREADY_EXIST_PHONENUMBER);
        }

        userRepository.save(user);
    }

    // 사용자 이메일로 정보 조회
    public UserDto getUserByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        UserDto userDto = new UserDto();
        userDto.setEmail(user.getEmail());
        userDto.setPassword(user.getPassword());
        userDto.setName(user.getName());
        userDto.setPhoneNumber(user.getPhoneNumber());
        userDto.setRole(user.getRole());
        return userDto;
    }

    // 비밀번호 변경
    @Transactional
    public void updatePassword(String email, String currentPassword, String newPassword) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserException(ExceptionType.NOT_FOUND_USER));

        if (!bCryptPasswordEncoder.matches(currentPassword, user.getPassword())) {
            throw new UserException(ExceptionType.WRONG_PASSWORD);
        }

        user.updatePassword(bCryptPasswordEncoder.encode(newPassword));
        authService.removeUserRefreshTokens(user);
    }
}
