package com.mamoru.transactionsystem.user.application;

import com.mamoru.transactionsystem.common.exception.ResourceNotFoundException;
import com.mamoru.transactionsystem.user.domain.Account;
import com.mamoru.transactionsystem.user.domain.User;
import com.mamoru.transactionsystem.user.infrastructure.repository.AccountRepository;
import com.mamoru.transactionsystem.user.infrastructure.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    
    @Mock
    private UserRepository userRepository;
    
    @Mock
    private AccountRepository accountRepository;
    
    @InjectMocks
    private UserService userService;
    
    private User user;
    
    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .username("testuser")
                .email("test@example.com")
                .build();
    }
    
    @Test
    void testCreateUser_Success() {
        when(userRepository.existsByUsername("testuser")).thenReturn(false);
        when(userRepository.existsByEmail("test@example.com")).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(accountRepository.save(any(Account.class))).thenAnswer(invocation -> invocation.getArgument(0));
        
        User createdUser = userService.createUser(user);
        
        assertNotNull(createdUser);
        assertEquals("testuser", createdUser.getUsername());
        verify(userRepository, times(1)).save(any(User.class));
        verify(accountRepository, times(1)).save(any(Account.class));
    }
    
    @Test
    void testCreateUser_UsernameExists() {
        when(userRepository.existsByUsername("testuser")).thenReturn(true);
        
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
                () -> userService.createUser(user));
        
        assertEquals("Username already exists: testuser", exception.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }
    
    @Test
    void testCreateUser_EmailExists() {
        when(userRepository.existsByUsername("testuser")).thenReturn(false);
        when(userRepository.existsByEmail("test@example.com")).thenReturn(true);
        
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
                () -> userService.createUser(user));
        
        assertEquals("Email already exists: test@example.com", exception.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }
    
    @Test
    void testGetUserById_Success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        
        User result = userService.getUserById(1L);
        
        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(userRepository, times(1)).findById(1L);
    }
    
    @Test
    void testGetUserById_NotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        
        assertThrows(ResourceNotFoundException.class, () -> userService.getUserById(1L));
        verify(userRepository, times(1)).findById(1L);
    }
}

