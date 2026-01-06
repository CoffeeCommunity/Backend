package coffee.community.backend.auth.exception;

import coffee.community.backend.global.exception.BusinessException;
import coffee.community.backend.global.i18n.MessageKey;
import org.springframework.http.HttpStatus;

public class AuthException extends BusinessException {

    public AuthException(MessageKey messageKey, HttpStatus status) {
        super(messageKey, status);
    }
}
