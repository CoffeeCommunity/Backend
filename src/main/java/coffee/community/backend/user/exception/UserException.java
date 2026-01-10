package coffee.community.backend.user.exception;

import coffee.community.backend.global.exception.BusinessException;
import coffee.community.backend.global.exception.ErrorCode;

public class UserException extends BusinessException {

    public UserException(ErrorCode errorCode) {
        super(errorCode.getMessageKey(), errorCode.getHttpStatus());
    }
}