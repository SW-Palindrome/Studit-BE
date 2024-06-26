package com.palindrome.studit.domain.user.domain;

import com.palindrome.studit.domain.study.domain.StudyEnrollment;
import com.palindrome.studit.global.utils.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
@Table(name = "users")
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @Column(length = 50, unique = true)
    @Size(max = 50)
    @NotNull
    private String email;

    @Column(length = 15)
    @Size(min = 2, max = 15)
    private String nickname;

    private String profileImage;

    @Column(name = "agree_tos")
    private boolean agreeTOS;

    @Column(name = "agree_picu")
    private boolean agreePICU;

    @Enumerated(EnumType.STRING)
    @NotNull
    private UserRoleType roleType;

    @OneToOne(mappedBy = "user", fetch = FetchType.LAZY)
    private OAuthInfo oAuthInfo;

    @OneToMany(mappedBy = "user")
    private List<StudyEnrollment> studyEnrollments = new ArrayList<>();

    public void changeNickname(String nickname) { this.nickname = nickname; }
}
