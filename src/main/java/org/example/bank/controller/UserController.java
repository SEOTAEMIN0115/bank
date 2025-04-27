package org.example.bank.controller;

import org.example.bank.dto.request.LoginRequest;
import org.example.bank.dto.request.SignupRequest;
import org.example.bank.dto.response.LoginResponse;
import org.example.bank.dto.response.SignupResponse;
import org.example.bank.entity.User;
import org.example.bank.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/signup")
    public ResponseEntity<SignupResponse> signup(@RequestBody SignupRequest request) {
        User user = userService.signup(request.getUsername(), request.getPassword(), request.getName());
        return ResponseEntity.ok(new SignupResponse(user.getId(), "회원가입 성공"));
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        User user = userService.login(request.getUsername(), request.getPassword());
        return ResponseEntity.ok(new LoginResponse(user.getId(), "로그인 성공"));
    }
}
