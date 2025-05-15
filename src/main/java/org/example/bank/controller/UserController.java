package org.example.bank.controller;

import java.util.List;

import org.example.bank.common.OperationResult;
import org.example.bank.dto.request.ChangePasswordRequest;
import org.example.bank.dto.request.ChangeRoleRequest;
import org.example.bank.dto.request.LoginRequest;
import org.example.bank.dto.request.SignupRequest;
import org.example.bank.dto.request.UpdatePasswordRequest;
import org.example.bank.dto.request.UpdateUserRoleRequest;
import org.example.bank.dto.request.UserRequest;
import org.example.bank.dto.response.LoginResponse;
import org.example.bank.dto.response.SignupResponse;
import org.example.bank.dto.response.UserDetailResponse;
import org.example.bank.dto.response.UserResponse;
import org.example.bank.entity.Role;
import org.example.bank.entity.User;
import org.example.bank.repository.UserRepository;
import org.example.bank.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserRepository userRepository;
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
    public ResponseEntity<UserResponse> getMyInfo() {
        User currentUser = getCurrentUser(); // 위에서 정의한 메서드 사용
        return ResponseEntity.ok(UserResponse.from(currentUser));
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

    @PutMapping("/users/change-role")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> changeUserRole(@RequestBody ChangeRoleRequest request) {
        userService.changeUserRole(request);
        return ResponseEntity.ok("User role updated successfully");
    }

    @DeleteMapping("/users/{userId}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<String> deleteUser(@PathVariable Long userId) {
        User currentUser = getCurrentUser();

        // 본인 또는 관리자만 삭제 가능
        if (!currentUser.getId().equals(userId) && !currentUser.getRole().equals(Role.ADMIN)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied.");
        }

        userService.deleteUser(userId);
        return ResponseEntity.ok("User deleted successfully.");
    }

    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalStateException("No authenticated user found");
        }

        return (Long) authentication.getPrincipal(); // principal에 userId가 들어감
    }

    private User getCurrentUser() {
        Long userId = getCurrentUserId();
        return userRepository.findById(userId)
            .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    @PutMapping("/users/password")
    public ResponseEntity<String> updatePassword(@RequestBody UpdatePasswordRequest request) {
        Long userId = getCurrentUserId(); // 이전에 만든 메서드 사용
        userService.updatePassword(userId, request);
        return ResponseEntity.ok("비밀번호가 변경되었습니다.");
    }

    @PostMapping("/users/deactivate")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> deactivateUser() {
        Long userId = getCurrentUserId();
        OperationResult result = userService.deactivateUser(userId);
        return buildResponse(result);
    }

    private ResponseEntity<?> buildResponse(OperationResult result) {
        if (result.isSuccess()) {
            return ResponseEntity.ok(result.getMessage());
        } else {
            return ResponseEntity.badRequest().body(result.getMessage());
        }
    }
}
