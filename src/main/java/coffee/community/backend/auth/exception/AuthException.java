package coffee.community.backend.auth.exception;

import coffee.community.backend.global.exception.BusinessException;

public class AuthException extends BusinessException {

    public AuthException(AuthErrorCode errorCode) {
        super(errorCode);
    }
}