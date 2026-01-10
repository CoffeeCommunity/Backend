package coffee.community.backend.global.i18n;

public enum AuthMessageKey implements MessageKey {

    LOGIN_FAILED("auth.login.failed"),
    TOKEN_EXPIRED("auth.token.expired"),
    UNAUTHORIZED("auth.unauthorized"),
    PHONE_NOT_VERIFIED("auth.phone.not.verified");

    private final String key;

    AuthMessageKey(String key) {
        this.key = key;
    }

    @Override
    public String key() {
        return key;
    }
}
