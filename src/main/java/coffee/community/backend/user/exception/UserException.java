package coffee.community.backend.user.exception;

import coffee.community.backend.global.exception.BusinessException;

public class UserException extends BusinessException {

    public UserException(UserErrorCode errorCode) {
        super(errorCode);
    }
}