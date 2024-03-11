package com.palindrome.studit.domain.user.api;

import com.palindrome.studit.domain.user.application.AuthService;
import com.palindrome.studit.domain.user.dao.OAuthInfoRepository;
import com.palindrome.studit.domain.user.dto.JwtDTO;
import com.palindrome.studit.domain.user.dto.RefreshTokenDTO;
import com.palindrome.studit.domain.user.entity.OAuthInfo;
import com.palindrome.studit.domain.user.exception.InvalidTokenException;
import com.palindrome.studit.global.config.security.application.TokenService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

@RequiredArgsConstructor
@RestController
@RequestMapping("/auth")
public class AuthController {
    private final TokenService tokenService;
    private final AuthService authService;

    @GetMapping("/token-info/{accessToken}")
    public JwtDTO getJwtInfo(@PathVariable String accessToken) throws InvalidTokenException {
        return JwtDTO.builder()
                .sub(tokenService.parseSubject(accessToken))
                .exp(tokenService.parseExpiration(accessToken))
                .build();
    }

    @PostMapping("/token-refresh")
    public ResponseEntity<String> refreshAccessToken(@RequestBody RefreshTokenDTO request) throws InvalidTokenException{
        String accessToken = authService.refreshAccessToken(request);
        return ResponseEntity.status(HttpStatus.OK).body(accessToken);
    }

    @ExceptionHandler({ InvalidTokenException.class })
    public ResponseEntity<Object> invalidTokenException(final InvalidTokenException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    }
}
