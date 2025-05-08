package org.example.bank.controller;

import lombok.RequiredArgsConstructor;
import org.example.bank.dto.response.AccountResponse;
import org.example.bank.dto.response.TransactionResponse;
import org.example.bank.dto.response.UserResponse;
import org.example.bank.entity.User;
import org.example.bank.repository.UserRepository;
import org.example.bank.service.AdminService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final UserRepository userRepository;
    private final AdminService adminService;

    @GetMapping("/users")
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        List<User> users = userRepository.findAll();
        List<UserResponse> responses = users.stream()
                .map(UserResponse::from)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    @DeleteMapping("/users/{userId}")
    public ResponseEntity<String> deleteUser(@PathVariable Long userId) {
        adminService.deleteUser(userId);
        return ResponseEntity.ok("사용자가 삭제되었습니다.");
    }

    @PatchMapping("/users/{userId}/role")
    public ResponseEntity<Void> updateUserRole(@PathVariable Long userId) {
        adminService.updateUserRole(userId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/accounts")
    public ResponseEntity<List<AccountResponse>> getAllAccounts() {
        List<AccountResponse> accounts = adminService.getAllAccounts();
        return ResponseEntity.ok(accounts);
    }

    @GetMapping("/transactions")
    public ResponseEntity<List<TransactionResponse>> getAllTransactions() {
        List<TransactionResponse> transactions = adminService.getAllTransactions();
        return ResponseEntity.ok(transactions);
    }
}
