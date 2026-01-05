package coffee.community.backend.auth.service;

import coffee.community.backend.auth.dto.*;

public interface AuthService {

    void signup(SignupRequest request);

    TokenResponse login(LoginRequest request);

    TokenResponse oauthLogin(OAuthLoginRequest request);

    void deleteMe(Long userId);
}