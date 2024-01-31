package com.palindrome.studit;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class StuditApplication {

	public static void main(String[] args) {
		SpringApplication.run(StuditApplication.class, args);
	}

}
