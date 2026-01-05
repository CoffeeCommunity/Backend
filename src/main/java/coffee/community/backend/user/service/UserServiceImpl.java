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
        user.updateNickname(request.getNickname());
        return UserResponse.from(user);
    }

    private User getUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND));
    }
}