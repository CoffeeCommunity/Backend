package coffee.community.backend.global.exception;

import coffee.community.backend.global.i18n.CommonMessageKey;
import org.springframework.http.HttpStatus;

public class AccessDeniedException extends BusinessException {
    public AccessDeniedException() {
        super(CommonMessageKey.ACCESS_DENIED, HttpStatus.FORBIDDEN);
    }
}