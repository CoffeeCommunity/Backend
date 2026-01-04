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
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            ObjectMapper objectMapper
    ) throws Exception {

        http
                // CSRF 비활성화 (JWT)
                .csrf(AbstractHttpConfigurer::disable)

                // 세션 사용 안 함
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                // 권한 설정
                .authorizeHttpRequests(auth -> auth
                        // Swagger
                        .requestMatchers(
                                "/swagger-ui/**",
                                "/v3/api-docs/**"
                        ).permitAll()

                        // Auth
                        .requestMatchers("/auth/**").permitAll()

                        // 조회 API 허용 예시
                        .requestMatchers(HttpMethod.GET, "/posts/**").permitAll()

                        // 나머지는 인증 필요
                        .anyRequest().authenticated()
                )

                // JWT 필터 등록
                .addFilterBefore(
                        jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class
                )

                // 인증 / 인가 예외 처리
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
    }
}
