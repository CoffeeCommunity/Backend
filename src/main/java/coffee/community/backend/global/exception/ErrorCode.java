package coffee.community.backend.global.exception;

import org.springframework.http.HttpStatus;

public interface ErrorCode {

    String getMessageKey();   // i18n 메시지 키
    HttpStatus getHttpStatus(); // HTTP 상태 코드
}