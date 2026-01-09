package coffee.community.backend.verification.exception;

import coffee.community.backend.global.common.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.Locale;

@Slf4j
@RestControllerAdvice(basePackages = "coffee.community.backend.verification")
public class VerificationExceptionHandler {

    private final MessageSource messageSource;

    public VerificationExceptionHandler(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    /**
     * &#064;Valid,  @Pattern 검증 실패 처리
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<String>> handleMethodArgumentNotValid(
            MethodArgumentNotValidException e, Locale locale) {
        return handleValidationErrors(e.getBindingResult(), locale);
    }

    @ExceptionHandler(BindException.class)
    public ResponseEntity<ApiResponse<String>> handleBindException(
            BindException e, Locale locale) {
        return handleValidationErrors(e.getBindingResult(), locale);
    }

    /**
     * 공통 검증 에러 처리
     */
    private ResponseEntity<ApiResponse<String>> handleValidationErrors(
            org.springframework.validation.BindingResult bindingResult, Locale locale) {
        List<FieldError> errors = bindingResult.getFieldErrors();
        String message = errors.stream()
                .map(fe -> {
                    String field = fe.getField();
                    String defaultMsg = fe.getDefaultMessage();
                    String msg = messageSource.getMessage("validation." + field,
                            null, defaultMsg, locale);
                    return field + ": " + msg;
                })
                .reduce((a, b) -> a + "; " + b)
                .orElse("검증 오류");

        log.warn("Verification validation failed: {}", message);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(message));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<String>> handleGeneral(Exception e) {
        log.error("Verification unexpected error", e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("인증 처리 중 오류 발생"));
    }
}