package coffee.community.backend.global.exception;

import coffee.community.backend.global.i18n.CommonMessageKey;
import org.springframework.http.HttpStatus;

public class SecurityConfigException extends BusinessException {
    public SecurityConfigException(Throwable cause) {
        super(CommonMessageKey.SECURITY_CONFIG_FAILED, HttpStatus.INTERNAL_SERVER_ERROR);
        initCause(cause);  // 원인 예외 체인
    }
}