package com.palindrome.studit.domain.user.api;

import com.palindrome.studit.domain.user.dto.JwtDTO;
import com.palindrome.studit.domain.user.exception.InvalidTokenException;
import com.palindrome.studit.global.config.security.application.TokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/auth")
public class AuthController {
    private final TokenService tokenService;

    @GetMapping("/token-info/{accessToken}")
    public JwtDTO getJwtInfo(@PathVariable String accessToken) throws InvalidTokenException {
        return JwtDTO.builder()
                .sub(tokenService.parseSubject(accessToken))
                .exp(tokenService.parseExpiration(accessToken))
                .build();
    }

    @ExceptionHandler({ InvalidTokenException.class })
    public ResponseEntity<Object> invalidTokenException(final InvalidTokenException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    }
}
