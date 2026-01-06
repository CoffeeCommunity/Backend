package coffee.community.backend.global.i18n;

public enum CommonMessageKey implements MessageKey {

    // auth
    AUTH_LOGIN_FAILED("auth.login.failed"),
    AUTH_TOKEN_EXPIRED("auth.token.expired"),
    AUTH_UNAUTHORIZED("auth.unauthorized"),
    AUTH_EMAIL_DUPLICATE("auth.email.duplicate"),
    AUTH_PHONE_NOT_VERIFIED("auth.phone.not.verified"),
    AUTH_PHONE_DUPLICATE("auth.phone.duplicate"),

    // user
    USER_NOT_FOUND("user.not-found"),
    USER_UNAUTHORIZED("user.unauthorized"),

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