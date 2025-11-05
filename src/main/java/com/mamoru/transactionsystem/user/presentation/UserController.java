package com.mamoru.transactionsystem.user.presentation;

import com.mamoru.transactionsystem.common.dto.ApiResponse;
import com.mamoru.transactionsystem.user.application.AccountService;
import com.mamoru.transactionsystem.user.application.UserService;
import com.mamoru.transactionsystem.user.domain.User;
import com.mamoru.transactionsystem.user.presentation.dto.AccountBalanceResponse;
import com.mamoru.transactionsystem.user.presentation.dto.AccountRechargeRequest;
import com.mamoru.transactionsystem.user.presentation.dto.UserRequest;
import com.mamoru.transactionsystem.user.presentation.dto.UserResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "User Management", description = "APIs for user and account management")
public class UserController {
    
    private final UserService userService;
    private final AccountService accountService;
    
    @PostMapping
    @Operation(summary = "Create a new user", description = "Creates a new user and automatically creates a prepaid account")
    public ResponseEntity<ApiResponse<UserResponse>> createUser(@Valid @RequestBody UserRequest request) {
        log.info("Creating user with username: {}", request.getUsername());
        
        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .build();
        
        User createdUser = userService.createUser(user);
        
        UserResponse response = UserResponse.builder()
                .id(createdUser.getId())
                .username(createdUser.getUsername())
                .email(createdUser.getEmail())
                .createdAt(createdUser.getCreatedAt())
                .updatedAt(createdUser.getUpdatedAt())
                .build();
        
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("User created successfully", response));
    }
    
    @GetMapping("/{userId}")
    @Operation(summary = "Get user by ID", description = "Retrieves user details by user ID")
    public ResponseEntity<ApiResponse<UserResponse>> getUser(
            @Parameter(description = "User ID", required = true) @PathVariable Long userId) {
        log.info("Fetching user with ID: {}", userId);
        
        User user = userService.getUserById(userId);
        
        UserResponse response = UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
        
        return ResponseEntity.ok(ApiResponse.success(response));
    }
    
    @PostMapping("/{userId}/accounts/recharge")
    @Operation(summary = "Recharge user account", description = "Recharges the user's prepaid account via mocked payment gateway")
    public ResponseEntity<ApiResponse<AccountBalanceResponse>> rechargeAccount(
            @Parameter(description = "User ID", required = true) @PathVariable Long userId,
            @Valid @RequestBody AccountRechargeRequest request) {
        log.info("Recharging account for user ID: {} with amount: {}", userId, request.getAmount());
        
        var account = accountService.rechargeAccount(userId, request.getAmount());
        
        AccountBalanceResponse response = AccountBalanceResponse.builder()
                .accountId(account.getId())
                .balance(account.getBalance())
                .currency(account.getCurrency())
                .build();
        
        return ResponseEntity.ok(ApiResponse.success("Account recharged successfully", response));
    }
    
    @GetMapping("/{userId}/accounts/balance")
    @Operation(summary = "Get account balance", description = "Retrieves the current balance of the user's prepaid account")
    public ResponseEntity<ApiResponse<AccountBalanceResponse>> getAccountBalance(
            @Parameter(description = "User ID", required = true) @PathVariable Long userId) {
        log.info("Fetching account balance for user ID: {}", userId);
        
        var balance = accountService.getBalanceByUserId(userId);
        var account = accountService.getAccountByUserId(userId);
        
        AccountBalanceResponse response = AccountBalanceResponse.builder()
                .accountId(account.getId())
                .balance(balance)
                .currency(account.getCurrency())
                .build();
        
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}

