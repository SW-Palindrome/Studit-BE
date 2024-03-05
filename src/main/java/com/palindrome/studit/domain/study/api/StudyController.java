package com.palindrome.studit.domain.study.api;

import com.palindrome.studit.domain.study.application.StudyService;
import com.palindrome.studit.domain.study.dto.CreateStudyDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/study")
public class StudyController {
    private final StudyService studyService;

    @PostMapping
    public ResponseEntity<Object> createStudy(Authentication authentication, @Valid @RequestBody CreateStudyDTO createStudyDTO) {
        studyService.createStudy(Long.parseLong(authentication.getName()), createStudyDTO);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
