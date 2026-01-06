package coffee.community.backend.user.exception;

import coffee.community.backend.global.exception.ErrorCode;
import coffee.community.backend.global.i18n.CommonMessageKey;
import coffee.community.backend.global.i18n.MessageKey;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum UserErrorCode implements ErrorCode {

    USER_NOT_FOUND(CommonMessageKey.USER_NOT_FOUND, HttpStatus.NOT_FOUND),
    UNAUTHORIZED(CommonMessageKey.USER_UNAUTHORIZED, HttpStatus.UNAUTHORIZED);

    private final MessageKey messageKey;
    private final HttpStatus httpStatus;

    UserErrorCode(MessageKey messageKey, HttpStatus httpStatus) {
        this.messageKey = messageKey;
        this.httpStatus = httpStatus;
    }

    @Override
    public MessageKey getMessageKey() {
        return messageKey;
    }

    @Override
    public HttpStatus getHttpStatus() {
        return httpStatus;
    }
}