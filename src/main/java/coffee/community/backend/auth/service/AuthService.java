package coffee.community.backend.auth.service;

import coffee.community.backend.auth.dto.*;

public interface AuthService {

    /**
     * 회원가입
     * @return 가입된 사용자 ID
     */
    Long signup(SignupRequest request);

    /**
     * 일반 로그인
     */
    LoginResponse login(LoginRequest request);

    /**
     * OAuth 로그인
     */
    LoginResponse oauthLogin(OAuthLoginRequest request);

    /**
     * 회원 탈퇴
     * @return 탈퇴 성공 여부
     */
    boolean deleteMe(Long userId);
}