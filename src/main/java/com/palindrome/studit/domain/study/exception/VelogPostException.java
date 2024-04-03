package com.palindrome.studit.domain.study.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class VelogPostException extends ResponseStatusException {
    public VelogPostException() {
        super(HttpStatus.NOT_FOUND, "Post not found");
    }
}
