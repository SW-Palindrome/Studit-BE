package com.palindrome.studit.domain.user.application;

import com.palindrome.studit.domain.user.dto.RefreshTokenDTO;
import com.palindrome.studit.domain.user.entity.OAuthInfo;
import com.palindrome.studit.domain.user.entity.OAuthProviderType;
import com.palindrome.studit.domain.user.entity.User;
import com.palindrome.studit.domain.user.entity.UserRoleType;
import com.palindrome.studit.domain.user.dao.OAuthInfoRepository;
import com.palindrome.studit.domain.user.dao.UserRepository;
import com.palindrome.studit.domain.user.exception.InvalidTokenException;
import com.palindrome.studit.global.config.security.application.TokenService;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final OAuthInfoRepository oAuthInfoRepository;
    private final TokenService tokenService;

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

    public void validateRefreshToken(Long userId, String refreshToken) throws InvalidTokenException{
        tokenService.parseExpiration(refreshToken);

        OAuthInfo oAuthInfo = oAuthInfoRepository.findByUser_UserId(userId);
        if (oAuthInfo == null) {
            throw new EntityNotFoundException();
        }
        if (!oAuthInfo.getRefreshToken().equals(refreshToken)) {
            throw new InvalidTokenException();
        }
    }

    public String refreshAccessToken(RefreshTokenDTO request) throws InvalidTokenException {
        Long userId = request.getUserId();
        String refreshToken = request.getRefreshToken();

        validateRefreshToken(userId, refreshToken);
        return tokenService.createAccessToken(Long.toString(userId));
    }
}
