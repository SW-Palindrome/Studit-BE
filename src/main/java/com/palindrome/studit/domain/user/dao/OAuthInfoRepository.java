package com.palindrome.studit.domain.user.dao;

import com.palindrome.studit.domain.user.domain.OAuthInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OAuthInfoRepository extends JpaRepository<OAuthInfo, Long> {
    OAuthInfo findByUser_UserId(Long userId);
}
