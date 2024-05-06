package com.palindrome.studit.domain.user.api;

import com.palindrome.studit.domain.user.application.UserService;
import com.palindrome.studit.domain.user.dto.ChangeNicknameRequestDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    @PatchMapping("/nickname")
    public ResponseEntity<Object> updateNickname(Authentication authentication, @Valid @RequestBody ChangeNicknameRequestDTO changeNicknameRequestDTO) {
        userService.changeNickname(Long.parseLong(authentication.getName()), changeNicknameRequestDTO.getNickname());
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
