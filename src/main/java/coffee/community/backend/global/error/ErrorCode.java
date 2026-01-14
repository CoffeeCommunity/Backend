package coffee.community.backend.global.error;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    AUTH_REQUIRED(HttpStatus.UNAUTHORIZED, "AUTH_REQUIRED", "error.auth.required"),
    ACCESS_DENIED(HttpStatus.FORBIDDEN, "ACCESS_DENIED", "error.access.denied");

    private final HttpStatus status;
    private final String code;
    private final String messageKey;
}