package com.palindrome.studit.domain.user.application;

import com.palindrome.studit.domain.user.entity.OAuthInfo;
import com.palindrome.studit.domain.user.entity.OAuthProviderType;
import com.palindrome.studit.domain.user.entity.User;
import com.palindrome.studit.domain.user.entity.UserRoleType;
import com.palindrome.studit.domain.user.dao.OAuthInfoRepository;
import com.palindrome.studit.domain.user.dao.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final OAuthInfoRepository oAuthInfoRepository;

    @Transactional
    public User createUser(String email, OAuthProviderType providerType, String providerId) {
        Optional<User> existUser = userRepository.findByEmail(email);
        if (existUser.isPresent()) {
            return existUser.get();
        }
        User user = User.builder().email(email).roleType(UserRoleType.USER).build();
        userRepository.save(user);
        OAuthInfo oAuthInfo = OAuthInfo.builder()
                .user(user)
                .provider(providerType)
                .providerId(providerId).build();
        oAuthInfoRepository.save(oAuthInfo);
        return user;
    }

    public void updateRefreshToken(User user, String refreshToken) {
        OAuthInfo oAuthInfo = oAuthInfoRepository.findByUser_UserId(user.getUserId());
        oAuthInfo.updateRefreshToken(refreshToken);
        oAuthInfoRepository.save(oAuthInfo);
    }
}
