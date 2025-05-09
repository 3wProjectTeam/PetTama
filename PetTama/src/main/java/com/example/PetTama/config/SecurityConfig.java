package com.example.PetTama.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /**
     * Spring Security 필터 체인 설정
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // CSRF 보호 비활성화 (RESTful API를 위해)
                .csrf(AbstractHttpConfigurer::disable)

                // 요청별 인증 설정
                .authorizeHttpRequests(auth -> auth
                        // 공개 리소스는 모두 접근 허용
                        .requestMatchers("/", "/home", "/index.html", "/*.html").permitAll()
                        .requestMatchers("/css/**", "/js/**", "/images/**").permitAll()
                        .requestMatchers("/auth/**", "/api/auth/**").permitAll()

                        // 게시판, 상점 API는 모두 접근 허용
                        .requestMatchers("/api/posts/**").permitAll()
                        .requestMatchers("/api/items/**").permitAll()
                        .requestMatchers("/board/**", "/shop/**").permitAll()

                        // 펫 관련 API는 인증 필요
                        .requestMatchers("/api/user-nums/**").authenticated()

                        // 기타 모든 요청은 인증 필요
                        .anyRequest().authenticated()
                )

                // 로그인 설정
                .formLogin(form -> form
                        .loginPage("/auth/login")  // 로그인 페이지 경로
                        .loginProcessingUrl("/api/auth/login")  // 로그인 처리 URL
                        .usernameParameter("email")  // 이메일 파라미터 이름
                        .passwordParameter("password")  // 비밀번호 파라미터 이름
                        .defaultSuccessUrl("/home", true)  // 로그인 성공 시 리다이렉트 URL
                        .failureUrl("/auth/login?error=true")  // 로그인 실패 시 리다이렉트 URL
                        .permitAll()
                )

                // 로그아웃 설정
                .logout(logout -> logout
                        .logoutUrl("/api/auth/logout")  // 로그아웃 URL
                        .logoutSuccessUrl("/home")  // 로그아웃 성공 시 리다이렉트 URL
                        .invalidateHttpSession(true)  // 세션 무효화
                        .clearAuthentication(true)  // 인증 정보 제거
                        .permitAll()
                );

        return http.build();
    }

    /**
     * 비밀번호 인코더 빈 정의
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * 인증 매니저 빈 정의
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }
}