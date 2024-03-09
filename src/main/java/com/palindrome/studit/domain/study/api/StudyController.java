package com.palindrome.studit.domain.study.api;

import com.palindrome.studit.domain.study.application.StudyService;
import com.palindrome.studit.domain.study.domain.Study;
import com.palindrome.studit.domain.study.dto.CreateStudyDTO;
import com.palindrome.studit.domain.study.dto.StudyResponseDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/studies")
public class StudyController {
    private final StudyService studyService;

    @PostMapping
    public ResponseEntity<Object> createStudy(Authentication authentication, @Valid @RequestBody CreateStudyDTO createStudyDTO) {
        studyService.createStudy(Long.parseLong(authentication.getName()), createStudyDTO);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping
    public Page<StudyResponseDTO> listStudies(@RequestParam(value = "page", defaultValue = "0") int page) {
        Page<Study> studies = studyService.getList(page);
        return StudyResponseDTO.toDTOPage(studies);
    }

    @GetMapping("/me")
    public Page<StudyResponseDTO> listMyStudies(Authentication authentication, @RequestParam(value = "page", defaultValue = "0") int page) {
        Page<Study> studies = studyService.getListByUser(page, Long.parseLong(authentication.getName()));
        return StudyResponseDTO.toDTOPage(studies);
    }
}
