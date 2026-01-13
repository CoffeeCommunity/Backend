package coffee.community.backend.global.exception;  // 적절한 패키지

import coffee.community.backend.global.i18n.CommonMessageKey;
import org.springframework.http.HttpStatus;

public class MsgSourceLoadFailedException extends BusinessException {
    public MsgSourceLoadFailedException(Throwable cause) {
        super(CommonMessageKey.MSG_SOURCE_LOAD_FAILED, HttpStatus.INTERNAL_SERVER_ERROR);
        initCause(cause);  // 원인 예외 체인
    }
}