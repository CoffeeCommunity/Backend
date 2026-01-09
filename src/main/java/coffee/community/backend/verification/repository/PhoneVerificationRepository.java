package coffee.community.backend.verification.repository;

import coffee.community.backend.verification.entity.PhoneVerification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface PhoneVerificationRepository extends JpaRepository<PhoneVerification, String> {

    Optional<PhoneVerification> findByPhoneNumber(String phoneNumber);

    @Modifying
    @Transactional
    @Query("DELETE FROM PhoneVerification p WHERE p.phoneNumber = :phoneNumber")
    int deleteByPhoneNumber(@Param("phoneNumber") String phoneNumber);

    @Modifying
    @Transactional
    @Query("DELETE FROM PhoneVerification p WHERE p.expireAt < CURRENT_TIMESTAMP")
    int deleteExpired();
}