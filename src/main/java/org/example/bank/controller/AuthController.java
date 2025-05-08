package org.example.bank.controller;

import java.util.Collections;

import org.example.bank.dto.request.LoginRequest;
import org.example.bank.dto.request.SignupRequest;
import org.example.bank.dto.response.LoginResponse;
import org.example.bank.dto.response.TokenResponse;
import org.example.bank.entity.User;
import org.example.bank.repository.UserRepository;
import org.example.bank.util.JwtTokenProvider;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        User user = userRepository.findByUsername(request.getUsername())
            .orElseThrow(() -> new IllegalArgumentException("사용자가 존재하지 않습니다."));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("비밀번호가 일치하지 않습니다.");
        }

        String token = jwtTokenProvider.createToken(user.getId() , user.getRole().name());
        System.out.println(token); // 임시 방편으로 사용
        return ResponseEntity.ok(Collections.singletonMap("token", token));
    }

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody SignupRequest request) {
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            return ResponseEntity.badRequest().body("이미 존재하는 사용자입니다.");
        }

        User user = new User(request.getUsername(),
            passwordEncoder.encode(request.getPassword()),
            request.getName());

        userRepository.save(user);
        return ResponseEntity.ok("회원가입 성공");
    }
}