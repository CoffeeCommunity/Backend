package coffee.community.backend.verification.service;

import coffee.community.backend.verification.entity.PhoneVerification;
import coffee.community.backend.verification.repository.PhoneVerificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
@Service
@RequiredArgsConstructor
public class PhoneVerificationServiceImpl implements PhoneVerificationService {

    private final PhoneVerificationRepository repository;

    private static final long CODE_EXPIRE_SECONDS = 180;

    @Override
    public boolean sendCode(String phoneNumber) {
        int deleted = repository.deleteByPhoneNumber(phoneNumber);
        log.debug("기존 인증 레코드 {}건 삭제됨", deleted);

        String code = generateCode();
        repository.save(new PhoneVerification(phoneNumber, code,
                Instant.now().plusSeconds(CODE_EXPIRE_SECONDS)));
        log.info("[PHONE VERIFY] phone={}에 인증번호 발송: {}", phoneNumber, code);
        return true;
    }

    @Override
    public boolean verifyCode(String phoneNumber, String code) {
        return repository.findByPhoneNumber(phoneNumber)
                .filter(pv -> Instant.now().isBefore(pv.getExpireAt()))
                .filter(pv -> pv.getCode().equals(code))
                .isPresent();
    }

    @Override
    public String issueToken(String phoneNumber) {
        String token = UUID.randomUUID().toString();
        repository.findByPhoneNumber(phoneNumber)
                .ifPresentOrElse(
                        pv -> {
                            pv.issueToken(token);
                            repository.save(pv);
                            log.info("[PHONE VERIFY] phone={}에 토큰 발급: {}", phoneNumber, token);
                        },
                        () -> log.warn("[PHONE VERIFY] phone={} 인증 정보 없음", phoneNumber)
                );
        return token;
    }

    @Override
    public boolean verifyToken(String phoneNumber, String token) {
        return repository.findByPhoneNumber(phoneNumber)
                .map(pv -> token.equals(pv.getToken()))
                .orElse(false);
    }

    private String generateCode() {
        int codeInt = ThreadLocalRandom.current().nextInt(100_000, 1_000_000);
        return String.format("%06d", codeInt);  // 6자리 고정 (앞 0 패딩)
    }

    @Scheduled(cron = "0 */5 * * * *")  // 5분마다 실행
    @Transactional
    public void cleanupExpired() {
        int deleted = repository.deleteExpired();
        if (deleted > 0) {
            log.info("만료된 인증 데이터 {}건 자동 삭제됨", deleted);
        }
    }
}