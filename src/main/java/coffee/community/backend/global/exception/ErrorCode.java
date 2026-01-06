package coffee.community.backend.global.exception;

import coffee.community.backend.global.i18n.MessageKey;
import org.springframework.http.HttpStatus;

public interface ErrorCode {

    MessageKey getMessageKey();   // i18n 메시지 키
    HttpStatus getHttpStatus(); // HTTP 상태 코드
}