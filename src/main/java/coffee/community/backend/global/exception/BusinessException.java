package coffee.community.backend.global.exception;

import coffee.community.backend.global.i18n.MessageKey;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.io.Serial;

@Getter
public abstract class BusinessException extends RuntimeException {

    private final transient MessageKey messageKey;
    private final HttpStatus httpStatus;

    @Serial
    private static final long serialVersionUID = 1L;

    protected BusinessException(MessageKey messageKey, HttpStatus httpStatus) {
        super(messageKey.key()); // RuntimeException 메시지는 String이면 충분
        this.messageKey = messageKey;
        this.httpStatus = httpStatus;
    }
}