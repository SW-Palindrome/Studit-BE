package com.palindrome.studit.domain.user.application;

import com.palindrome.studit.domain.user.dao.UserRepository;
import com.palindrome.studit.domain.user.domain.OAuthProviderType;
import com.palindrome.studit.domain.user.domain.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class UserServiceTest {
    @Autowired
    private UserService userService;

    @Autowired
    private AuthService authService;

    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("사용자 닉네임 변경 테스트")
    void changeNicknameTest() {
        //Given
        User user = authService.createUser("user@email.com", OAuthProviderType.GITHUB, "providerId");

        //When
        userService.changeNickname(user.getUserId(), "변경된 닉네임");

        //Then
        assertThat(userRepository.findById(user.getUserId()).orElseThrow().getNickname()).isEqualTo("변경된 닉네임");
    }

}