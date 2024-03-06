package com.palindrome.studit.user.repository;

import com.palindrome.studit.domain.user.dao.UserRepository;
import com.palindrome.studit.domain.user.domain.User;
import com.palindrome.studit.domain.user.domain.UserRoleType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
public class UserRepositoryTest {

    @Autowired
    UserRepository userRepository;

    @Test
    @DisplayName("유저 생성 테스트")
    void createUserTest() {
        //Given
        User user = User.builder()
                .email("swm.palindrome@gmail.com")
                .nickname("palindrome")
                .profileImage("https://palworld.shwa.space/assets/PalIcon/T_PinkCat_icon_normal.png")
                .agreeTOS(true)
                .agreePICU(true)
                .roleType(UserRoleType.ADMIN)
                .build();

        userRepository.save(user);

        //When
        User savedUser = userRepository.findByNickname("palindrome");

        //Then
        assertThat(savedUser.getEmail()).isEqualTo("swm.palindrome@gmail.com");
        assertThat(savedUser.getRoleType()).isEqualTo(UserRoleType.ADMIN);
        assertThat(savedUser.getCreatedDate()).isNotNull();
    }

    @Test
    @DisplayName("테스트 데이터 조회")
    void findAllUsersTest() {
        //Given: data.sql

        //When
        List<User> users = userRepository.findAll();

        //Then
        assertThat(users).isNotEmpty();
        assertThat(users.get(0).getNickname()).isEqualTo("USER1");
        assertThat(users.get(1).getNickname()).isEqualTo("USER2");
    }
}
