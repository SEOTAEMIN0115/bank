package org.example.bank.service;

import java.util.List;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;

import org.example.bank.dto.request.ChangePasswordRequest;
import org.example.bank.dto.request.ChangeRoleRequest;
import org.example.bank.dto.request.UserRequest;
import org.example.bank.dto.response.UserDetailResponse;
import org.example.bank.dto.response.UserResponse;
import org.example.bank.entity.Role;
import org.example.bank.entity.User;
import org.example.bank.repository.UserRepository;
import org.example.bank.util.JwtTokenProvider;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;

    public void signup(UserRequest request) {
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new IllegalArgumentException("이미 존재하는 사용자입니다.");
        }
        String encodedPw = passwordEncoder.encode(request.getPassword());
        User user = new User(request.getUsername(),
            encodedPw,
            request.getName());
        userRepository.save(user);
    }

    public String login(UserRequest request) {
        User user = userRepository.findByUsername(request.getUsername())
            .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }
        return jwtTokenProvider.createToken(user.getId(), user.getRole().name());
    }

    public UserResponse getMyInfo(Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("사용자 없음"));
        return new UserResponse(user.getId(), user.getUsername(), user.getRole().name() , user.getName());
    }

    @Transactional
    public void changePassword(Long userId, ChangePasswordRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new IllegalArgumentException("기존 비밀번호가 일치하지 않습니다.");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }

    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream()
            .map(UserResponse::from)
            .collect(Collectors.toList());
    }

    @Transactional
    public void updateUserRole(Long userId, String roleName) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("회원을 찾을 수 없습니다."));

        Role newRole = Role.valueOf(roleName.toUpperCase());
        user.setRole(newRole);
    }

    @Transactional(readOnly = true)
    public UserDetailResponse getUserById(Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("회원을 찾을 수 없습니다."));

        return UserDetailResponse.from(user);
    }

    @Transactional
    public void changeUserRole(ChangeRoleRequest request) {
        User user = userRepository.findById(request.getUserId())
            .orElseThrow(() -> new IllegalArgumentException("User not found"));

        user.setRole(Role.valueOf(request.getNewRole().toUpperCase())); // Enum 타입에 따라 조정 필요
        userRepository.save(user);
    }

    @Transactional
    public void deleteUser(Long userId) {
        userRepository.deleteById(userId);
    }
}
