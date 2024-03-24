package com.palindrome.studit.domain.study.domain;

import com.palindrome.studit.global.utils.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class Study extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long studyId;

    @Size(max = 30)
    private String name;

    @NotNull
    private LocalDateTime startAt;

    @NotNull
    private LocalDateTime endAt;

    @NotNull
    private Long maxMembers;

    @Size(max = 255)
    private String studyImage;

    @NotNull
    @Enumerated(EnumType.STRING)
    private StudyPurpose purpose;

    private String description;

    @NotNull
    private Boolean isPublic;

    @NotNull
    @Enumerated(EnumType.STRING)
    private StudyStatus status;

    @Embedded
    private MissionInfo mission;

    @Size(max = 5)
    @Column(unique = true)
    private String shareCode;

    @OneToMany(mappedBy = "study")
    private List<StudyEnrollment> enrollments = new ArrayList<>();

    public void start() {

        this.status = StudyStatus.IN_PROGRESS;
    }
}
