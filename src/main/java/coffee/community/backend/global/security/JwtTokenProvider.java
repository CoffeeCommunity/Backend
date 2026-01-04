package coffee.community.backend.global.security;

import io.jsonwebtoken.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JwtTokenProvider {

    @Value("${jwt.secret}")
    private String secretKey;

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(secretKey.getBytes())
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (Exception _) {
            return false;
        }
    }

    public Authentication getAuthentication(String token, HttpServletRequest request) {
        UserDetails userDetails = loadUserByToken(token);

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );

        authentication.setDetails(
                new WebAuthenticationDetailsSource().buildDetails(request)
        );

        return authentication;
    }

    // ✅ 임시 사용자
    private UserDetails loadUserByToken(String token) {
        return org.springframework.security.core.userdetails.User
                .withUsername("temporary-user")
                .password("")
                .authorities("ROLE_USER")
                .build();
    }
}
