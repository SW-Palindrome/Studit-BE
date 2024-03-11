package com.palindrome.studit.domain.study.exception;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
public class DuplicatedStudyEnrollmentException extends RuntimeException {
    private String message = "";
    @Override
    public String getMessage() {
        if (message != null && !message.isEmpty()) {
            return message;
        }
        return super.getMessage();
    }
}
