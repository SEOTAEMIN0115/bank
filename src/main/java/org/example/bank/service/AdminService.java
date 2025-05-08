package org.example.bank.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.bank.entity.User;
import org.example.bank.repository.AccountRepository;
import org.example.bank.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final UserRepository userRepository;
    private final AccountRepository accountRepository;

    @Transactional
    public void deleteUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        // 연관된 계좌 삭제
        accountRepository.deleteAllByUserId(userId);
        userRepository.delete(user);
    }
}
