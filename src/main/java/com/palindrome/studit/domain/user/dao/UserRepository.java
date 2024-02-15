package com.palindrome.studit.domain.user.dao;

import com.palindrome.studit.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    User findByNickname(String nickname);
    Optional<User> findByEmail(String email);
}
