package com.ll.jwt_2025_01_07;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class Jwt20250107Application {

    public static void main(String[] args) {
        SpringApplication.run(Jwt20250107Application.class, args);
    }

}
