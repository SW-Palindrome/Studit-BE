package com.palindrome.studit.domain.user.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.Date;

@Entity
public class User {

    @Id
    @Column(name = "user_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "", length=50, unique = true)
    @NotNull
    private String email;

    @Size(min=2, max=15)
    private String nickname;

    @NotNull
    private String profileImage;

    @NotNull
    private boolean agreeTOS;

    @NotNull
    private boolean agreePICU;

    @Enumerated(EnumType.STRING)
    @NotNull
    private UserRole role;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at")
    private Date createdDate;

}
