package com.palindrome.studit.domain.user.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
public class OAuthInfo extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @OneToOne
    @JoinColumn(name="user_id")
    private User user;

    @Enumerated(EnumType.STRING)
    private OAuthProviderType provider;

    @Size(max = 50)
    @NotBlank
    private String providerId;

    @Size(max = 40)
    private String refreshToken;

    private LocalDateTime expiredDate;

    @Builder
    public OAuthInfo(User user, OAuthProviderType provider, String providerId) {
        this.user = user;
        this.provider = provider;
        this.providerId = providerId;
    }
}
