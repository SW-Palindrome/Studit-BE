package com.palindrome.studit.utils;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@SpringBootTest
@ActiveProfiles("local")
public class PostgresqlDbConfigTest {

    @Value("${DB_URL}")
    private String URL;

    @Value("${DB_USERNAME}")
    private String USERNAME;

    @Value("${DB_PASSWORD}")
    private String PASSWORD;


    @Test
    @DisplayName("postgresql db 연결 테스트")
    void DbConnectionTest() throws SQLException {
        Connection conn = DriverManager.getConnection(URL,USERNAME,PASSWORD);
        log.info("연결 정보 확인: {}", conn);
        assertThat(conn).isNotNull();
    }
}
