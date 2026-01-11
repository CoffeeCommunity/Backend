package coffee.community.backend.global.config;

import coffee.community.backend.global.common.ApiResponse;
import coffee.community.backend.global.security.JwtAuthenticationEntryPoint;
import coffee.community.backend.global.security.JwtAuthenticationFilter;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    /** ✅ PasswordEncoder Bean */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /** ✅ Security Filter Chain */
    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            ObjectMapper objectMapper
    ) {

        try {
            http
                    // CSRF 비활성화 (JWT)
                    .csrf(AbstractHttpConfigurer::disable)

                    // 세션 미사용
                    .sessionManagement(session ->
                            session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                    )

                    // 권한 설정
                    .authorizeHttpRequests(auth -> auth
                            // swagger
                            .requestMatchers(
                                    "/swagger-ui/**",
                                    "/v3/api-docs/**"
                            ).permitAll()

                            // ✅ 인증 없이 가능한 AUTH API (POST만!)
                            .requestMatchers(HttpMethod.POST,
                                    "/auth/signup",
                                    "/auth/login",
                                    "/auth/oauth/login",
                                    "/auth/phone/send",
                                    "/auth/phone/verify"
                            ).permitAll()

                            // ✅ 공개 GET API
                            .requestMatchers(HttpMethod.GET, "/posts/**").permitAll()

                            // ❌ 그 외는 전부 인증 필요
                            .anyRequest().authenticated()
                    )

                    // JWT 필터
                    .addFilterBefore(
                            jwtAuthenticationFilter,
                            UsernamePasswordAuthenticationFilter.class
                    )

                    // 예외 처리
                    .exceptionHandling(ex -> ex
                            .authenticationEntryPoint(jwtAuthenticationEntryPoint) // 401
                            .accessDeniedHandler((request, response, accessDeniedException) -> {

                                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                                response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                                response.setCharacterEncoding("UTF-8");

                                ApiResponse<String> body =
                                        ApiResponse.error("접근 권한이 없습니다.");

                                objectMapper.writeValue(response.getWriter(), body);
                            })
                    );

            return http.build();

        } catch (Exception e) {
            // ❗ Security 설정 실패는 치명적이므로 런타임 예외로 감싸서 올림
            throw new IllegalStateException("SecurityConfig 설정 중 오류 발생", e);
        }
    }
}