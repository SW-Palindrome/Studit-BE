package com.palindrome.studit.domain.user.entity;

import jakarta.persistence.*;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@Getter
public class BaseEntity {

    @Column(name = "created_at")
    @CreatedDate
    private LocalDateTime createdDate;

    @Column(name = "updated_at")
    @LastModifiedDate
    private LocalDateTime updatedDate;

    @Column(name = "deleted_at")
    private LocalDateTime deletedDate;

}
