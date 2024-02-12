package com.palindrome.studit.user.repository;

import com.palindrome.studit.domain.user.dao.UserRepository;
import com.palindrome.studit.domain.user.entity.Member;
import com.palindrome.studit.domain.user.entity.UserRoleType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2, replace = AutoConfigureTestDatabase.Replace.NONE)
public class MemberRepositoryTest {

    @Autowired
    UserRepository userRepository;

    @Test
    @DisplayName("유저 생성 테스트")
    void createUserTest() {
        //Given
        Member member = Member.builder()
                .email("swm.palindrome@gmail.com")
                .nickname("palindrome")
                .profileImage("https://palworld.shwa.space/assets/PalIcon/T_PinkCat_icon_normal.png")
                .agreeTOS(true)
                .agreePICU(true)
                .roleType(UserRoleType.ADMIN)
                .build();

        userRepository.save(member);

        //When
        Member savedMember = userRepository.findByNickname("palindrome");

        //Then
        assertThat(savedMember.getEmail()).isEqualTo("swm.palindrome@gmail.com");
        assertThat(savedMember.getRoleType()).isEqualTo(UserRoleType.ADMIN);
        assertThat(savedMember.getCreatedDate()).isNotNull();
    }

    @Test
    @DisplayName("테스트 데이터 조회")
    void findAllUsersTest() {
        //Given: data.sql

        //When
        List<Member> members = userRepository.findAll();

        //Then
        assertThat(members).isNotEmpty();
        assertThat(members.get(0).getNickname()).isEqualTo("USER1");
        assertThat(members.get(1).getNickname()).isEqualTo("USER2");
    }
}
