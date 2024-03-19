package com.palindrome.studit.global.error;

import lombok.*;
import org.springframework.http.HttpStatus;

@Data
@AllArgsConstructor
public class ErrorResponse {

    private String code;
    private String message;
    private HttpStatus status;

}
