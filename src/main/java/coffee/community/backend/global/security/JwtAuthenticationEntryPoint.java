package coffee.community.backend.global.security;

import coffee.community.backend.global.common.ApiResponse;
import coffee.community.backend.global.error.ErrorCode;
import coffee.community.backend.global.error.ErrorResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;

    @Override
    public void commence(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull AuthenticationException authException
    ) throws IOException {

        int status = HttpServletResponse.SC_UNAUTHORIZED;

        response.setStatus(status);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");

        ApiResponse<ErrorResponse> body =
                ApiResponse.error(
                        status,
                        ErrorCode.AUTH_REQUIRED.getMessage(),
                        ErrorResponse.from(ErrorCode.AUTH_REQUIRED)
                );

        objectMapper.writeValue(response.getWriter(), body);
    }
}