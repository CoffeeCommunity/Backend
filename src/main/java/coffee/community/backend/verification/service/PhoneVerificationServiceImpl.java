package coffee.community.backend.verification.service;

import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class PhoneVerificationServiceImpl implements PhoneVerificationService {

    /** 인증번호 저장소 (phone -> code) */
    private final Map<String, String> codeStore = new ConcurrentHashMap<>();

    /** 인증 만료 시간 (phone -> expireAt) */
    private final Map<String, Instant> expireStore = new ConcurrentHashMap<>();

    /** 인증 완료 토큰 (token -> phone) */
    private final Map<String, String> tokenStore = new ConcurrentHashMap<>();

    private static final long CODE_EXPIRE_SECONDS = 180; // 3분

    @Override
    public boolean sendCode(String phoneNumber) {
        String code = generateCode();

        codeStore.put(phoneNumber, code);
        expireStore.put(phoneNumber, Instant.now().plusSeconds(CODE_EXPIRE_SECONDS));

        // 실제 서비스면 여기서 SMS 발송
        System.out.println("[PHONE VERIFY] phone=" + phoneNumber + ", code=" + code);

        return true;
    }

    @Override
    public boolean verifyCode(String phoneNumber, String code) {
        String savedCode = codeStore.get(phoneNumber);
        Instant expireAt = expireStore.get(phoneNumber);

        if (savedCode == null || expireAt == null) {
            return false;
        }

        if (Instant.now().isAfter(expireAt)) {
            codeStore.remove(phoneNumber);
            expireStore.remove(phoneNumber);
            return false;
        }

        return savedCode.equals(code);
    }

    @Override
    public String issueToken(String phoneNumber) {
        String token = UUID.randomUUID().toString();
        tokenStore.put(token, phoneNumber);
        return token;
    }

    @Override
    public boolean verifyToken(String phoneNumber, String token) {
        String verifiedPhone = tokenStore.get(token);
        return phoneNumber.equals(verifiedPhone);
    }

    private String generateCode() {
        int code = (int) (Math.random() * 900000) + 100000;
        return String.valueOf(code);
    }
}