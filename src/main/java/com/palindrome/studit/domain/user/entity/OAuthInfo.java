package com.palindrome.studit.domain.user.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
public class OAuthInfo extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @Enumerated(EnumType.STRING)
    private OAuthProviderType provider;

    @Size(max = 50)
    @NotBlank
    private String proverId;

    @Size(max = 40)
    @NotBlank
    private String refreshToken;

    @NotNull
    private LocalDateTime expiredDate;
}
