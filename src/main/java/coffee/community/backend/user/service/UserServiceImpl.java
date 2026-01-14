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
        User user = getUser(userId);

        if (request.getNickname() != null &&
                !request.getNickname().equals(user.getNickname())) {

            if (!isNicknameAvailable(request.getNickname())) {
                throw new UserException(UserErrorCode.NICKNAME_DUPLICATE);
            }
            user = user.updateNickname(request.getNickname());
        }

        if (request.getPhoneNumber() != null &&
                !request.getPhoneNumber().equals(user.getPhoneNumber())) {

            if (!isPhoneNumberAvailable(request.getPhoneNumber())) {
                throw new UserException(UserErrorCode.PHONE_NUMBER_DUPLICATE);
            }
            user = user.updatePhoneNumber(request.getPhoneNumber());
        }

        user = user.updateBio(request.getBio())
                .updateProfileImageUrl(request.getProfileImageUrl());

        return UserResponse.from(user);
    }

    private User getUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND));
    }

    private boolean isPhoneNumberAvailable(String phoneNumber) {
        return !userRepository.existsByPhoneNumberAndDeletedFalse(phoneNumber);
    }

    private boolean isNicknameAvailable(String nickname) {
        return !userRepository.existsByNickname(nickname); // 필요 시 AndDeletedFalse
    }
}