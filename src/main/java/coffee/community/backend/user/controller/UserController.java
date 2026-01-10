package coffee.community.backend.user.controller;

import coffee.community.backend.global.common.ApiResponse;
import coffee.community.backend.global.util.SecurityUtil;
import coffee.community.backend.user.dto.UserResponse;
import coffee.community.backend.user.dto.UserUpdateRequest;
import coffee.community.backend.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    public ApiResponse<UserResponse> getMyInfo() {
        Long userId = SecurityUtil.getCurrentUserId();
        return ApiResponse.ok(userService.getMyInfo(userId));
    }

    @PutMapping("/me")
    public ApiResponse<UserResponse> updateMyInfo(
            @RequestBody @Valid UserUpdateRequest request
    ) {
        Long userId = SecurityUtil.getCurrentUserId();
        return ApiResponse.ok(userService.updateMyInfo(userId, request));
    }
}