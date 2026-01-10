package coffee.community.backend.global.util;

import coffee.community.backend.global.exception.AuthCommonErrorCode;
import coffee.community.backend.global.exception.UnauthorizedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public final class SecurityUtil {

    private SecurityUtil() {
        throw new IllegalStateException("Utility class");
    }

    public static Long getCurrentUserId() {
        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || authentication.getPrincipal() == null) {
            throw new UnauthorizedException(AuthCommonErrorCode.UNAUTHORIZED);
        }

        return (Long) authentication.getPrincipal();
    }
}