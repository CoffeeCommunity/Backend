package coffee.community.backend.global.common;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ApiResponse<T> {

    private final int status;
    private final String message;
    private final T data;

    private ApiResponse(int status, String message, T data) {
        this.status = status;
        this.message = message;
        this.data = data;
    }

    /* ================= 성공 응답 ================= */

    public static <T> ApiResponse<T> ok(T data) {
        return new ApiResponse<>(
                HttpStatus.OK.value(),
                "SUCCESS",
                data
        );
    }

    public static <T> ApiResponse<T> ok(T data, String customMessage) {
        return new ApiResponse<>(
                HttpStatus.OK.value(),
                customMessage,
                data
        );
    }

    /** data 없이 메시지만 */
    public static <T> ApiResponse<T> ok(String customMessage) {
        return new ApiResponse<>(
                HttpStatus.OK.value(),
                customMessage,
                null
        );
    }

    /* ================= 에러 응답 ================= */

    public static <T> ApiResponse<T> error(int status, String message, T data) {
        return new ApiResponse<>(
                status,
                message,
                data
        );
    }

    public static <T> ApiResponse<T> error(String message) {
        return new ApiResponse<>(
                HttpStatus.BAD_REQUEST.value(),
                message,
                null
        );
    }
}