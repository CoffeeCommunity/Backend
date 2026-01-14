package coffee.community.backend.global.exception;

import coffee.community.backend.global.i18n.CommonMessageKey;
import coffee.community.backend.global.i18n.MessageKey;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum AuthCommonErrorCode implements ErrorCode {

    UNAUTHORIZED(CommonMessageKey.AUTH_UNAUTHORIZED, HttpStatus.UNAUTHORIZED);

    private final MessageKey messageKey;
    private final HttpStatus httpStatus;

    AuthCommonErrorCode(MessageKey messageKey, HttpStatus httpStatus) {
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