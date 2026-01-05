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

    public static ApiResponse<Void> ok() {
        return new ApiResponse<>(
                HttpStatus.OK.value(),
                "SUCCESS",
                null
        );
    }

    /* ================= 에러 응답 ================= */

    /** 상태 코드 직접 지정 */
    public static <T> ApiResponse<T> error(int status, String message, T data) {
        return new ApiResponse<>(
                status,
                message,
                data
        );
    }

    /** ⭐ 가장 많이 쓰는 기본 에러 */
    public static <T> ApiResponse<T> error(String message) {
        return new ApiResponse<>(
                HttpStatus.BAD_REQUEST.value(),
                message,
                null
        );
    }
}