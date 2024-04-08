package com.palindrome.studit.domain.user.application;

import com.palindrome.studit.domain.user.dao.UserRepository;
import com.palindrome.studit.domain.user.domain.User;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    @Transactional
    public void changeNickname(Long userId, String nickname) {
        User user = userRepository.findById(userId).orElseThrow();
        user.changeNickname(nickname);
    }
}
