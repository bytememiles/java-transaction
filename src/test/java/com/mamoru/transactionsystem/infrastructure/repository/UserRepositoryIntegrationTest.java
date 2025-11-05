package com.mamoru.transactionsystem.infrastructure.repository;

import com.mamoru.transactionsystem.user.domain.User;
import com.mamoru.transactionsystem.user.infrastructure.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
@Tag("integration")
class UserRepositoryIntegrationTest {
    
    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine")
            .withDatabaseName("test_transaction_system")
            .withUsername("test")
            .withPassword("test");
    
    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }
    
    @Autowired
    private UserRepository userRepository;
    
    private User user;
    
    @BeforeEach
    void setUp() {
        user = User.builder()
                .username("testuser")
                .email("test@example.com")
                .build();
    }
    
    @Test
    void testSaveUser() {
        User savedUser = userRepository.save(user);
        
        assertNotNull(savedUser.getId());
        assertEquals("testuser", savedUser.getUsername());
    }
    
    @Test
    void testFindByUsername() {
        userRepository.save(user);
        
        Optional<User> found = userRepository.findByUsername("testuser");
        
        assertTrue(found.isPresent());
        assertEquals("testuser", found.get().getUsername());
    }
    
    @Test
    void testFindByEmail() {
        userRepository.save(user);
        
        Optional<User> found = userRepository.findByEmail("test@example.com");
        
        assertTrue(found.isPresent());
        assertEquals("test@example.com", found.get().getEmail());
    }
    
    @Test
    void testExistsByUsername() {
        userRepository.save(user);
        
        assertTrue(userRepository.existsByUsername("testuser"));
        assertFalse(userRepository.existsByUsername("nonexistent"));
    }
}

