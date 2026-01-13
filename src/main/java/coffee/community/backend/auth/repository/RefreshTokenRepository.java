package coffee.community.backend.auth.repository;

import coffee.community.backend.auth.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RefreshTokenRepository
        extends JpaRepository<RefreshToken, Long> {

    long deleteAllByUserId(Long userId);

    long deleteByToken(String token);
}
