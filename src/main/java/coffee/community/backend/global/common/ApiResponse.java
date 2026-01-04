package coffee.community.backend.global.common;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ApiResponse<T> {

    private boolean success;
    private String message;
    private T data;

    /* 성공 응답 */
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(true, null, data);
    }

    /* 에러 응답 */
    public static ApiResponse<String> error(String message) {
        return new ApiResponse<>(false, message, null);
    }
}