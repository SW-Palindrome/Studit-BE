package com.palindrome.studit.domain.user.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.Date;

@Builder
@Getter
public class JwtDTO {
    private String sub;
    private Date exp;
}
