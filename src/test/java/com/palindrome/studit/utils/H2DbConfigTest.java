package com.palindrome.studit.utils;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
public class H2DbConfigTest {

    @Autowired
    private DataSource dataSource;

    @Test
    @DisplayName("h2 db 연결 테스트")
    void DbConnectionTest() throws SQLException {
        Connection conn = dataSource.getConnection();
        assertThat(conn).isNotNull();
    }
}
