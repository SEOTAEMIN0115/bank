package org.example.bank.service;

import lombok.RequiredArgsConstructor;
import org.example.bank.dto.response.AccountResponse;
import org.example.bank.dto.response.TransactionResponse;
import org.example.bank.dto.response.UserResponse;
import org.example.bank.entity.Account;
import org.example.bank.entity.Role;
import org.example.bank.entity.Transaction;
import org.example.bank.entity.User;
import org.example.bank.repository.AccountRepository;
import org.example.bank.repository.TransactionRepository;
import org.example.bank.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final UserRepository userRepository;
    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;

    @Transactional
    public void deleteUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        // 연관된 계좌 삭제
        accountRepository.deleteAllByUserId(userId);
        userRepository.delete(user);
    }

    public List<UserResponse> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(UserResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional
    public void updateUserRole(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        user.setRole(Role.ADMIN); // USER → ADMIN
    }

    @Transactional(readOnly = true)
    public List<AccountResponse> getAllAccounts() {
        List<Account> accounts = accountRepository.findAll();

        return accounts.stream()
                .map(AccountResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<TransactionResponse> getAllTransactions() {
        List<Transaction> transactions = transactionRepository.findAll();

        return transactions.stream()
                .map(TransactionResponse::from)
                .collect(Collectors.toList());
    }
}
