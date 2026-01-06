package coffee.community.backend.global.exception;

import coffee.community.backend.global.i18n.MessageKey;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public abstract class BusinessException extends RuntimeException {

    private final MessageKey messageKey;
    private final HttpStatus httpStatus;

    protected BusinessException(MessageKey messageKey, HttpStatus httpStatus) {
        super(messageKey.key()); // RuntimeException 메시지는 String이면 충분
        this.messageKey = messageKey;
        this.httpStatus = httpStatus;
    }
}
