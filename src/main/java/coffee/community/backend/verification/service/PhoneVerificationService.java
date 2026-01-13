package coffee.community.backend.verification.service;

public interface PhoneVerificationService {

    /**
     * 인증번호 발송
     * @return 발송 성공 여부
     */
    boolean sendCode(String phoneNumber);

    /**
     * 인증번호 검증
     * @return 검증 성공 여부
     */
    boolean verifyCode(String phoneNumber, String code);

    /**
     * 인증 성공 후 토큰 발급
     */
    String issueToken(String phoneNumber);

    /**
     * 회원가입 시 토큰 검증
     * @return 검증 성공 여부
     */
    boolean verifyToken(String phoneNumber, String token);

    int getRetryCount(String phoneNumber);
}