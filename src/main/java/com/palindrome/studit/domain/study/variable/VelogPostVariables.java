package com.palindrome.studit.domain.study.variable;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class VelogPostVariables implements Variables{

    private String username;
    private String tag;
}
