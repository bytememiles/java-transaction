package com.mamoru.transactionsystem.common.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;

@TestConfiguration
public class TestConfig {
    
    @Container
    public static PostgreSQLContainer<?> postgresContainer = new PostgreSQLContainer<>("postgres:15-alpine")
            .withDatabaseName("test_transaction_system")
            .withUsername("test")
            .withPassword("test");
    
    static {
        postgresContainer.start();
    }
}

