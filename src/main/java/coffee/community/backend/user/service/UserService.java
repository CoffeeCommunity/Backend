package coffee.community.backend.user.service;

import coffee.community.backend.user.dto.UserResponse;
import coffee.community.backend.user.dto.UserUpdateRequest;

public interface UserService {

    UserResponse getMyInfo(Long userId);

    UserResponse updateMyInfo(Long userId, UserUpdateRequest request);
}