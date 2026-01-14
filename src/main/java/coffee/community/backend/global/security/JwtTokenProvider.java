package coffee.community.backend.global.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.List;

@Component
public class JwtTokenProvider {

    private final Key key;

    private final long accessTokenExpirationTime;
    private final long refreshTokenExpirationTime;

    public JwtTokenProvider(
            @Value("${jwt.secret}") String secretKey,
            @Value("${jwt.access-token-expiration-ms}") long accessTokenExpirationTime,
            @Value("${jwt.refresh-token-expiration-ms}") long refreshTokenExpirationTime
    ) {
        this.key = Keys.hmacShaKeyFor(
                Decoders.BASE64.decode(secretKey)
        );
        this.accessTokenExpirationTime = accessTokenExpirationTime;
        this.refreshTokenExpirationTime = refreshTokenExpirationTime;
    }

    /* ================= 토큰 생성 ================= */

    /** Access Token 생성 */
    public String createAccessToken(Long userId) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + accessTokenExpirationTime);

        return Jwts.builder()
                .setSubject(String.valueOf(userId))
                .setIssuedAt(now)
                .setExpiration(expiry)
                .claim("type", "access")
                .signWith(key)
                .compact();
    }

    /** Refresh Token 생성 */
    public String createRefreshToken(Long userId) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + refreshTokenExpirationTime);

        return Jwts.builder()
                .setSubject(String.valueOf(userId))
                .setIssuedAt(now)
                .setExpiration(expiry)
                .claim("type", "refresh")
                .signWith(key)
                .compact();
    }

    /* ================= 토큰 검증 ================= */

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException _) {
            return false;
        }
    }

    /* ================= Authentication 생성 ================= */

    public Authentication getAuthentication(String token, HttpServletRequest request) {
        Long userId = getUserId(token);

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(
                        userId,
                        null,
                        List.of(new SimpleGrantedAuthority("ROLE_USER"))
                );

        authentication.setDetails(
                new WebAuthenticationDetailsSource().buildDetails(request)
        );

        return authentication;
    }

    /* ================= Claims / userId ================= */

    public Long getUserId(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();

        return Long.valueOf(claims.getSubject());
    }

    /* ================= 만료 시간 ================= */

    public long getAccessTokenExpireSeconds() {
        return accessTokenExpirationTime / 1000;
    }

    public long getRefreshTokenExpireSeconds() {
        return refreshTokenExpirationTime / 1000;
    }

    public boolean validateRefreshToken(String refreshToken) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(refreshToken)
                    .getBody();

            // type 확인
            String type = claims.get("type", String.class);
            return "refresh".equals(type);

        } catch (JwtException | IllegalArgumentException _) {
            return false;
        }
    }

    public Long getUserIdFromRefreshToken(String refreshToken) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(refreshToken)
                .getBody();

        // subject에 userId를 String으로 넣어놨기 때문에 Long으로 변환
        return Long.valueOf(claims.getSubject());
    }
}