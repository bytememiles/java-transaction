package com.mamoru.transactionsystem.common.config;

import io.github.cdimascio.dotenv.Dotenv;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.nio.file.Paths;

/**
 * Configuration class to load environment variables from .env file.
 * This allows the application to use .env files for configuration.
 */
@Configuration
@Slf4j
public class DotEnvConfig {
    
    @Bean
    public Dotenv dotenv() {
        try {
            // Try to load .env from project root
            Dotenv dotenv = Dotenv.configure()
                    .directory(Paths.get("").toAbsolutePath().toString())
                    .ignoreIfMissing()
                    .load();
            
            // Set system properties from .env file so Spring Boot can use them
            dotenv.entries().forEach(entry -> {
                String key = entry.getKey();
                String value = entry.getValue();
                if (System.getProperty(key) == null && System.getenv(key) == null) {
                    System.setProperty(key, value);
                }
            });
            
            log.info("Loaded {} environment variables from .env file", dotenv.entries().size());
            return dotenv;
        } catch (Exception e) {
            log.warn("Could not load .env file, using system environment variables: {}", e.getMessage());
            return Dotenv.configure().ignoreIfMissing().load();
        }
    }
}

