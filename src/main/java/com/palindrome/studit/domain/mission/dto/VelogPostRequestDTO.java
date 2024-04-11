package com.palindrome.studit.domain.study.dto;

import com.palindrome.studit.domain.mission.variable.Variables;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class VelogPostRequestDTO {
    private String queryFieldName;
    private Variables variables;
    private String query;
}
