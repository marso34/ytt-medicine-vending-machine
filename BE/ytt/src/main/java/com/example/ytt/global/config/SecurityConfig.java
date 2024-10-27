package com.example.ytt.global.config;

import com.example.ytt.domain.user.auth.jwt.*;
import com.example.ytt.domain.user.auth.security.CustomLogoutFilter;
import com.example.ytt.domain.user.auth.security.LoginFailHandler;
import com.example.ytt.domain.user.auth.security.LoginFilter;
import com.example.ytt.domain.user.auth.security.LoginSuccessHandler;
import com.example.ytt.domain.user.repository.RefreshRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import java.util.Collections;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final AuthenticationConfiguration authenticationConfiguration;
    private final JWTUtil jwtUtil;
    private final LoginSuccessHandler loginSuccessHandler;
    private final LoginFailHandler loginFailHandler;
    private final RefreshRepository refreshRepository;

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // 로그인 인증 필터
    @Bean
    public LoginFilter loginFilter() throws Exception {
        LoginFilter loginFilter = new LoginFilter(authenticationManager(authenticationConfiguration), jwtUtil);
        loginFilter.setAuthenticationManager(authenticationManager(authenticationConfiguration));
        loginFilter.setAuthenticationSuccessHandler(loginSuccessHandler);  // 로그인 성공했을 때 실행시킬 핸들러
        loginFilter.setAuthenticationFailureHandler(loginFailHandler);      // 로그인 실패했을 때 실행시킬 핸들러
        loginFilter.setFilterProcessesUrl("/user/signIn");  // 로그인 경로 /login -> /user/signIn 으로 변경
        return loginFilter;
    }


    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        // CORS 설정
        http
                .cors((cors) -> cors
                        .configurationSource(new CorsConfigurationSource() {
                            @Override
                            public CorsConfiguration getCorsConfiguration(HttpServletRequest request) {

                                CorsConfiguration configuration = new CorsConfiguration();

                                configuration.setAllowedOrigins(Collections.singletonList("http://localhost:3000"));
                                configuration.setAllowedMethods(Collections.singletonList("*"));
                                configuration.setAllowCredentials(true);
                                configuration.setAllowedHeaders(Collections.singletonList("*"));
                                configuration.setMaxAge(3600L);

                                configuration.setExposedHeaders(Collections.singletonList("Authorization"));

                                return configuration;
                            }
                        })
                );
        // 개발시 h2 사용위한 설정 차후 제거
        http
                .csrf((csrf) -> csrf.ignoringRequestMatchers("/h2-console/**"))  // H2 콘솔에 대한 CSRF 비활성화
                .headers((headers) -> headers.frameOptions((frame) -> frame.sameOrigin()));  // 동일 출처에서 프레임 허용

        http
                .csrf((auth) -> auth.disable()) // csrf disable
                .formLogin((auth) -> auth.disable()) // Form 로그인 방식 disable
                .httpBasic((auth) -> auth.disable()); //http basic 인증 방식 disable

        // 경로별 인가 작업
        http
                .authorizeHttpRequests((auth) -> auth
                        .requestMatchers("/api-docs/**", "/swagger-ui/**", "/v3/api-docs/**").permitAll()
                        .requestMatchers("/user/signUp","/user/signIn", "/h2-console/**").permitAll()
                        .requestMatchers("/user/reissue").permitAll()
                        .anyRequest().authenticated());

        // 로그인 필터 설정
        http
                .addFilterBefore(new JWTFilter(jwtUtil), LoginFilter.class)
                .addFilterAt(loginFilter(), UsernamePasswordAuthenticationFilter.class);
        http
                .addFilterBefore(new CustomLogoutFilter(jwtUtil, refreshRepository), LogoutFilter.class);

        // 세션설정
        http
                .sessionManagement((session) -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        return http.build();
    }
}