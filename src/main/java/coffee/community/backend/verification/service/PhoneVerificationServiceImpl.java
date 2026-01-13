package coffee.community.backend.verification.service;

import coffee.community.backend.verification.entity.PhoneVerification;
import coffee.community.backend.verification.repository.PhoneVerificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import lombok.extern.slf4j.Slf4j;
import net.nurigo.sdk.message.model.Message;
import net.nurigo.sdk.message.request.SingleMessageSendingRequest;
import net.nurigo.sdk.message.response.SingleMessageSentResponse;
import net.nurigo.sdk.message.service.DefaultMessageService;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
@Service
@RequiredArgsConstructor
public class PhoneVerificationServiceImpl implements PhoneVerificationService {

    private final PhoneVerificationRepository repository;
    private final DefaultMessageService messageService;
    private final MessageSource messageSource;

    @Value("${coolsms.from-number}")
    private String fromNumber;

    private static final long CODE_EXPIRE_SECONDS = 300;
    private static final String SMS_MESSAGE_KEY = "sms.verification.code";

    @Override
    @Transactional
    public boolean sendCode(String phoneNumber) {
        int deleted = repository.deleteByPhoneNumber(phoneNumber);
        log.debug("기존 인증 레코드 {}건 삭제됨", deleted);

        String code = generateCode();
        PhoneVerification pv = new PhoneVerification(phoneNumber, code,
                Instant.now().plusSeconds(CODE_EXPIRE_SECONDS));
        repository.save(pv);

        try {
            Message message = new Message();
            message.setFrom(fromNumber);
            message.setTo(phoneNumber);

            Locale locale = LocaleContextHolder.getLocale();
            String smsText = messageSource.getMessage(SMS_MESSAGE_KEY,
                    new Object[]{code}, locale);
            message.setText(smsText);

            SingleMessageSentResponse response = messageService.sendOne(
                    new SingleMessageSendingRequest(message));

            log.info("[PHONE VERIFY SMS] {}에 발송 성공: {} (groupId={})",
                    phoneNumber, code, Objects.requireNonNull(response).getGroupId());

            return true;

        } catch (Exception e) {
            log.error("SMS 전송 실패 phone={}, code={}", phoneNumber, code, e);
            repository.deleteByPhoneNumber(phoneNumber);
            return false;
        }
    }

    @Override
    public boolean verifyCode(String phoneNumber, String code) {
        return repository.findByPhoneNumber(phoneNumber)
                .filter(pv -> Instant.now().isBefore(pv.getExpireAt()))
                .filter(pv -> pv.getCode().equals(code))
                .isPresent();
    }

    @Override
    @Transactional
    public String issueToken(String phoneNumber) {
        String token = java.util.UUID.randomUUID().toString();
        repository.findByPhoneNumber(phoneNumber)
                .ifPresentOrElse(
                        pv -> {
                            repository.save(pv.issueToken(token));
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
        int codeInt = ThreadLocalRandom.current().nextInt(1000, 10000);
        return String.format("%04d", codeInt);
    }

    @Scheduled(cron = "0 */5 * * * *")
    @Transactional
    public int cleanupExpiredJob() {
        int deleted = repository.deleteExpired();

        if (deleted > 0) {
            log.info("만료된 인증 데이터 {}건 자동 삭제됨", deleted);
        }

        return deleted;
    }

    @Override
    public int getRetryCount(String phoneNumber) {
        return repository.findByPhoneNumber(phoneNumber)
                .map(PhoneVerification::getRetryCount)
                .orElse(0);
    }
}