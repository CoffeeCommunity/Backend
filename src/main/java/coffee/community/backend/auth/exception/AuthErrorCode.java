package coffee.community.backend.auth.exception;

import coffee.community.backend.global.exception.ErrorCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum AuthErrorCode implements ErrorCode {

    EMAIL_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "AUTH_001", "auth.email.duplicate"),
    INVALID_CREDENTIALS(HttpStatus.UNAUTHORIZED, "AUTH_002", "auth.login.failed"),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "AUTH_003", "auth.user.notfound"),
    PHONE_NOT_VERIFIED(HttpStatus.BAD_REQUEST, "AUTH_004", "auth.phone.not.verified");

    private final HttpStatus httpStatus;
    private final String code;
    private final String messageKey;

    AuthErrorCode(HttpStatus httpStatus, String code, String messageKey) {
        this.httpStatus = httpStatus;
        this.code = code;
        this.messageKey = messageKey;
    }
}