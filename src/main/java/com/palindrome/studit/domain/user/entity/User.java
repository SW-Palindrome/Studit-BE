package com.palindrome.studit.domain.user.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Entity
@NoArgsConstructor
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "USER_ID")
    private Long userId;

    @Column(length = 50, unique = true)
    @Size(max = 50)
    @NotNull
    private String email;

    @Column(length = 15)
    @Size(min = 2, max = 15)
    @NotBlank
    private String nickname;

    @Column(name = "PROFILE_IMAGE")
    @NotNull
    private String profileImage;

    @Column(name = "AGREE_TOS")
    @NotNull
    private boolean agreeTOS;

    @Column(name = "AGREE_PICU")
    @NotNull
    private boolean agreePICU;

    @Column(name = "ROLE_TYPE")
    @Enumerated(EnumType.STRING)
    @NotNull
    private UserRoleType roleType;

}
