package coffee.community.backend.global.exception;

import org.springframework.http.HttpStatus;

public enum AuthCommonErrorCode implements ErrorCode {

    UNAUTHORIZED(
            HttpStatus.UNAUTHORIZED,
            "auth.unauthorized"
    );

    private final HttpStatus httpStatus;
    private final String messageKey;

    AuthCommonErrorCode(HttpStatus httpStatus, String messageKey) {
        this.httpStatus = httpStatus;
        this.messageKey = messageKey;
    }

    @Override
    public String getMessageKey() {
        return messageKey;
    }

    @Override
    public HttpStatus getHttpStatus() {
        return httpStatus;
    }
}