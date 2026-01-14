package coffee.community.backend.auth.exception;

import coffee.community.backend.global.i18n.MessageKey;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum AuthErrorCode implements MessageKey {

    PHONE_NOT_VERIFIED(
            "auth.phone.not.verified",
            HttpStatus.BAD_REQUEST
    ),
    EMAIL_ALREADY_EXISTS(
            "user.duplicate-email",
            HttpStatus.CONFLICT
    ),
    NICKNAME_ALREADY_EXISTS("user.duplicate-nickname", HttpStatus.CONFLICT),
    UNAUTHORIZED("auth.unauthorized", HttpStatus.UNAUTHORIZED),
    PHONE_ALREADY_EXISTS(
            "user.duplicate-phone",
            HttpStatus.CONFLICT
    ),
    USER_NOT_FOUND(
            "user.not-found",
            HttpStatus.NOT_FOUND
    ),
    INVALID_CREDENTIALS(
            "auth.login.failed",
            HttpStatus.UNAUTHORIZED
    ),
    INVALID_REFRESH_TOKEN(
            "auth.invalid.refresh-token",
            HttpStatus.UNAUTHORIZED
    );

    private final String messageKey;
    private final HttpStatus httpStatus;

    AuthErrorCode(String messageKey, HttpStatus httpStatus) {
        this.messageKey = messageKey;
        this.httpStatus = httpStatus;
    }

    @Override
    public String key() {
        return messageKey;
    }
}