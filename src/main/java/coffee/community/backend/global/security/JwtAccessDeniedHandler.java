package coffee.community.backend.global.security;

import coffee.community.backend.global.common.ApiResponse;
import coffee.community.backend.global.error.ErrorCode;
import coffee.community.backend.global.error.ErrorResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAccessDeniedHandler implements AccessDeniedHandler {

    private final ObjectMapper objectMapper;
    private final MessageSource messageSource;

    @Override
    public void handle(
            HttpServletRequest request,
            HttpServletResponse response,
            AccessDeniedException accessDeniedException
    ) throws IOException {

        ErrorCode errorCode = ErrorCode.ACCESS_DENIED;
        int status = errorCode.getStatus().value();

        String message = messageSource.getMessage(
                errorCode.getMessageKey(),
                null,
                request.getLocale()
        );

        response.setStatus(status);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");

        ApiResponse<ErrorResponse> body =
                ApiResponse.error(
                        status,
                        message,
                        new ErrorResponse(errorCode.getCode(), message)
                );

        objectMapper.writeValue(response.getWriter(), body);
    }
}
