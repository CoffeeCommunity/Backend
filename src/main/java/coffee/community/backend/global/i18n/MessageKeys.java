package coffee.community.backend.global.i18n;

/**
 * 다국어 메시지 키 상수 모음
 * - ErrorCode에서 messageKey로 사용
 * - messages_ko.yml / messages_en.yml 과 1:1 매핑
 */
public final class MessageKeys {

    private MessageKeys() {
        // 인스턴스 생성 방지
    }

    /* =========================
       Auth
       ========================= */
    public static final class Auth {
        public static final String LOGIN_FAILED = "auth.login.failed";
        public static final String TOKEN_EXPIRED = "auth.token.expired";
        public static final String UNAUTHORIZED = "auth.unauthorized";
    }

    /* =========================
       User
       ========================= */
    public static final class User {
        public static final String NOT_FOUND = "user.not-found";
        public static final String UNAUTHORIZED = "user.unauthorized";
        public static final String DUPLICATE_EMAIL = "user.duplicate-email";
    }

    /* =========================
       Post
       ========================= */
    public static final class Post {
        public static final String NOT_FOUND = "post.not-found";
        public static final String FORBIDDEN = "post.forbidden";
    }

    /* =========================
       Comment
       ========================= */
    public static final class Comment {
        public static final String NOT_FOUND = "comment.not-found";
        public static final String FORBIDDEN = "comment.forbidden";
    }

    /* =========================
       Follow
       ========================= */
    public static final class Follow {
        public static final String ALREADY_FOLLOWING = "follow.already-following";
        public static final String NOT_FOLLOWING = "follow.not-following";
    }

    /* =========================
       Bookmark
       ========================= */
    public static final class Bookmark {
        public static final String ALREADY_BOOKMARKED = "bookmark.already-bookmarked";
        public static final String NOT_BOOKMARKED = "bookmark.not-bookmarked";
    }

    /* =========================
       Like
       ========================= */
    public static final class Like {
        public static final String ALREADY_LIKED = "like.already-liked";
        public static final String NOT_LIKED = "like.not-liked";
    }

    /* =========================
       Bean
       ========================= */
    public static final class Bean {
        public static final String NOT_FOUND = "bean.not-found";
    }

    /* =========================
       Cafe
       ========================= */
    public static final class Cafe {
        public static final String NOT_FOUND = "cafe.not-found";
    }

    /* =========================
       Recipe
       ========================= */
    public static final class Recipe {
        public static final String NOT_FOUND = "recipe.not-found";
    }

    /* =========================
       Tag
       ========================= */
    public static final class Tag {
        public static final String NOT_FOUND = "tag.not-found";
    }

    /* =========================
       Report
       ========================= */
    public static final class Report {
        public static final String INVALID_TARGET = "report.invalid-target";
    }

    /* =========================
       Notification
       ========================= */
    public static final class Notification {
        public static final String NOT_FOUND = "notification.not-found";
    }

    /* =========================
       SearchLog
       ========================= */
    public static final class SearchLog {
        public static final String INVALID_KEYWORD = "searchlog.invalid-keyword";
    }
}