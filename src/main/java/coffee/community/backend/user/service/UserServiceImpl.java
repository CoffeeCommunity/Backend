package coffee.community.backend.user.service;

import coffee.community.backend.user.dto.UserResponse;
import coffee.community.backend.user.dto.UserUpdateRequest;
import coffee.community.backend.user.entity.User;
import coffee.community.backend.user.exception.UserErrorCode;
import coffee.community.backend.user.exception.UserException;
import coffee.community.backend.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public UserResponse getMyInfo(Long userId) {
        User user = getUser(userId);
        return UserResponse.from(user);
    }

    @Override
    @Transactional
    public UserResponse updateMyInfo(Long userId, UserUpdateRequest request) {
        User user = getUser(userId)
                .updateNickname(request.getNickname())
                .updateBio(request.getBio())  // 옵셔널하게
                .updateProfileImageUrl(request.getProfileImageUrl());  // 옵셔널하게
        return UserResponse.from(user);
    }

    private User getUser(Long userId) {
        return userRepository.findByIdAndDeletedFalse(userId)
                .orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND));
    }

    // 중복 체크 private 메서드
    private void validatePhoneNumberNotDuplicate(String phoneNumber) {
        if (userRepository.existsByPhoneNumberAndDeletedFalse(phoneNumber)) {
            throw new UserException(UserErrorCode.PHONE_NUMBER_DUPLICATE);
        }
    }

    // 닉네임 등에도 유사 추가 (이미 existsByNickname 있음)
    private void validateNicknameNotDuplicate(String nickname) {
        if (userRepository.existsByNickname(nickname)) {  // 또는 AndDeletedFalse
            throw new UserException(UserErrorCode.NICKNAME_DUPLICATE);
        }
    }
}