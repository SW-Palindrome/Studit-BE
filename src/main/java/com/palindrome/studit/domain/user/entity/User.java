package com.palindrome.studit.domain.user.entity;

import com.palindrome.studit.global.utils.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
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

    @NotNull
    private String profileImage;

    @Column(name = "agree_tos")
    @NotNull
    private boolean agreeTOS;

    @Column(name = "agree_picu")
    @NotNull
    private boolean agreePICU;

    @Enumerated(EnumType.STRING)
    @NotNull
    private UserRoleType roleType;

}
