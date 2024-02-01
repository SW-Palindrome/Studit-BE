package com.palindrome.studit.model;

import com.palindrome.studit.enums.OAuthProviderType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class OAuthInfo {
    private OAuthProviderType oAuthProviderType;
    private String providerId;
    private User user;
}
