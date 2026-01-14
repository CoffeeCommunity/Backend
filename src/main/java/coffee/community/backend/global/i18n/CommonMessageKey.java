package coffee.community.backend.global.i18n;

public enum CommonMessageKey implements MessageKey {

    // auth
    AUTH_LOGIN_FAILED("auth.login.failed"),
    AUTH_TOKEN_EXPIRED("auth.token.expired"),
    AUTH_UNAUTHORIZED("auth.unauthorized"),

    // security
    ACCESS_DENIED("access.denied"),  // 접근 권한 없음
    SECURITY_CONFIG_FAILED("security.config.failed"),  // 보안 설정 실패

    PHONE_NUMBER_DUPLICATE("phone-number.duplicate"),
    EMAIL_DUPLICATE("email.duplicate"),
    NICKNAME_DUPLICATE("nickname.duplicate"),

    // user
    USER_NOT_FOUND("user.not-found"),
    USER_UNAUTHORIZED("user.unauthorized"),
    USER_NICKNAME_DUPLICATE("user.nickname.duplicate"),
    MSG_SOURCE_LOAD_FAILED("msg.source.load.failed"),

    // post
    POST_NOT_FOUND("post.not-found");

    private final String key;

    CommonMessageKey(String key) {
        this.key = key;
    }

    @Override
    public String key() {
        return key;
    }
}