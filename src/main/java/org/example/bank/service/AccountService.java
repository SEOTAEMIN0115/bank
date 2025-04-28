package org.example.bank.service;

import org.example.bank.dto.request.CreateAccountRequest;
import org.example.bank.dto.response.AccountResponse;
import org.example.bank.entity.Account;
import org.example.bank.entity.User;
import org.example.bank.repository.AccountRepository;
import org.example.bank.repository.UserRepository;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class AccountService {

    private final AccountRepository accountRepository;
    private final UserRepository userRepository;

    public AccountService(AccountRepository accountRepository, UserRepository userRepository) {
        this.accountRepository = accountRepository;
        this.userRepository = userRepository;
    }

    public AccountResponse createAccount(CreateAccountRequest request) {
        User user = userRepository.findById(request.getUserId())
            .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        Account account = new Account(request.getAccountNumber(), 0L, user);
        Account savedAccount = accountRepository.save(account);

        return new AccountResponse(savedAccount.getId(), savedAccount.getAccountNumber(), savedAccount.getBalance());
    }

    public AccountResponse getAccount(Long accountId) {
        Account account = accountRepository.findById(accountId)
            .orElseThrow(() -> new IllegalArgumentException("계좌를 찾을 수 없습니다."));

        return new AccountResponse(account.getId(), account.getAccountNumber(), account.getBalance());
    }
}
