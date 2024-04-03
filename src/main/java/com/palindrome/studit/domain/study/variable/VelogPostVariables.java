package com.palindrome.studit.domain.study.variable;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Builder
@Getter
public class VelogPostsVariables implements Variables{

    private String username;
    private String tag;
}
