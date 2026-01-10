package coffee.community.backend.user.repository;

import coffee.community.backend.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);
    boolean existsByPhoneNumber(String phoneNumber);
    boolean existsByNickname(String nickname);  // 닉네임 중복 체크 추가!

    Optional<User> findByIdAndDeletedFalse(Long id);

    boolean existsByPhoneNumberAndDeletedFalse(String phoneNumber);
}