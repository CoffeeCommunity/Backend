package coffee.community.backend.verification.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;

import java.time.Instant;

@Entity
@Table(name = "phone_verification", indexes = @Index(columnList = "phoneNumber"))
@Getter
@NoArgsConstructor
public class PhoneVerification {

    @Id
    @Column(unique = true, length = 20)
    private String phoneNumber;

    @Column(length = 4)
    private String code;

    @Column(name = "expire_at")
    private Instant expireAt;

    @Column(name = "token")
    private String token;

    @CreatedDate
    @Column(name = "created_at")
    private Instant createdAt;

    // Setter 추가 (Lombok @Setter 대신 수동)
    @Setter
    @Column(name = "retry_count", nullable = false, columnDefinition = "int default 0")
    private int retryCount = 0;

    // 생성자
    public PhoneVerification(String phoneNumber, String code, Instant expireAt) {
        this.phoneNumber = phoneNumber;
        this.code = code;
        this.expireAt = expireAt;
        this.createdAt = Instant.now();
        this.retryCount = 0;  // 명시적 초기화
    }

    public PhoneVerification issueToken(String token) {
        this.token = token;
        return this;
    }
}