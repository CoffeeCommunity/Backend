package coffee.community.backend.global.exception;

public class UnauthorizedException extends BusinessException {

    public UnauthorizedException(ErrorCode errorCode) {
        super(errorCode.getMessageKey(), errorCode.getHttpStatus());
    }
}