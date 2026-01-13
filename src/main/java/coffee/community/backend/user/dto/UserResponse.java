package coffee.community.backend.user.dto;

import coffee.community.backend.user.entity.User;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserResponse {

    private Long id;
    private String email;
    private String nickname;
    private String profileImageUrl;
    private String bio;
    private String phoneNumber;

    public static UserResponse from(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .nickname(user.getNickname())
                .profileImageUrl(user.getProfileImageUrl())
                .bio(user.getBio())
                .phoneNumber(maskPhoneNumber(user.getPhoneNumber()))  // 보안
                .build();
    }

    // 전화번호 마스킹 (010-****-1234)
    private static String maskPhoneNumber(String phoneNumber) {
        if (phoneNumber == null) return null;
        return phoneNumber.replaceAll("(\\d{3})(\\d{4})(\\d{4})", "$1-****-$3");
    }
}