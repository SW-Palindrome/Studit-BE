package com.palindrome.studit.domain.user.dto;

import lombok.*;

@Getter
public class RefreshTokenDTO {
    private Long userId;
    private String refreshToken;
}
