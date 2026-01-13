package coffee.community.backend.global.error;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    AUTH_REQUIRED("AUTH_REQUIRED", "error.auth.required"),
    ACCESS_DENIED("ACCESS_DENIED", "error.access.denied"),
    MSG_SOURCE_LOAD_FAILED("MSG_SOURCE_LOAD_FAILED", "error.msg.source.load.failed");

    private final String code;
    private final String message;
}