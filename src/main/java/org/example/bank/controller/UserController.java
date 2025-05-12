package org.example.bank.controller;

import java.util.List;

import org.example.bank.dto.request.ChangePasswordRequest;
import org.example.bank.dto.request.LoginRequest;
import org.example.bank.dto.request.SignupRequest;
import org.example.bank.dto.request.UpdateUserRoleRequest;
import org.example.bank.dto.request.UserRequest;
import org.example.bank.dto.response.LoginResponse;
import org.example.bank.dto.response.SignupResponse;
import org.example.bank.dto.response.UserDetailResponse;
import org.example.bank.dto.response.UserResponse;
import org.example.bank.entity.User;
import org.example.bank.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/signup")
    public ResponseEntity<Void> signup(@RequestBody UserRequest request) {
        userService.signup(request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody UserRequest request) {
        String token = userService.login(request);
        return ResponseEntity.ok(token);
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponse> getMyInfo(Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();
        return ResponseEntity.ok(userService.getMyInfo(userId));
    }

    @PatchMapping("/password")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> changePassword(@RequestBody ChangePasswordRequest request) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) auth.getPrincipal();

        userService.changePassword(user.getId(), request);
        return ResponseEntity.ok("비밀번호가 성공적으로 변경되었습니다.");
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        List<UserResponse> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @PatchMapping("/{userId}/role")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> updateUserRole(
        @PathVariable Long userId,
        @RequestBody UpdateUserRoleRequest request) {

        userService.updateUserRole(userId, request.getRole());
        return ResponseEntity.ok("회원 역할이 성공적으로 변경되었습니다.");
    }

    @GetMapping("/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserDetailResponse> getUserById(@PathVariable Long userId) {
        UserDetailResponse response = userService.getUserById(userId);
        return ResponseEntity.ok(response);
    }
}
