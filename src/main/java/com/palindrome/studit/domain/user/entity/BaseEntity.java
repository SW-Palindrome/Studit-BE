package com.palindrome.studit.domain.user.entity;

import jakarta.persistence.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public class BaseEntity {

    @Column(name = "CREATED_AT")
    @CreatedDate
    private LocalDateTime createdDate;

    @Column(name = "UPDATED_AT")
    @LastModifiedDate
    private LocalDateTime updatedDate;

    @Column(name = "DELETED_AT")
    private LocalDateTime deletedDate;

}
