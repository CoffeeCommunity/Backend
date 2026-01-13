package coffee.community.backend.auth.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class PhoneSendResponse {
    private String phoneNumber;
    private String message;
    private int retryCount;
    private LocalDateTime expiresAt;  // 만료시간 등
}
