package org.example.bank.service;


import java.util.List;
import java.util.stream.Collectors;

import org.example.bank.common.OperationResult;
import org.example.bank.dto.request.CreateAccountRequest;
import org.example.bank.dto.request.DepositRequest;
import org.example.bank.dto.request.TransferRequest;
import org.example.bank.dto.request.WithdrawRequest;
import org.example.bank.dto.response.AccountResponse;
import org.example.bank.dto.response.TransactionResponse;
import org.example.bank.entity.Account;
import org.example.bank.entity.Transaction;
import org.example.bank.entity.TransactionType;
import org.example.bank.entity.User;
import org.example.bank.repository.AccountRepository;
import org.example.bank.repository.TransactionRepository;
import org.example.bank.repository.UserRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;
    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;

    public AccountResponse createAccount(CreateAccountRequest request) {
        User user = userRepository.findById(request.getUserId())
            .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        Account account = new Account(request.getAccountNumber(), 0L, user);
        Account savedAccount = accountRepository.save(account);

        return new AccountResponse(savedAccount.getId(), savedAccount.getAccountNumber(), savedAccount.getBalance());
    }

    public AccountResponse getAccount(Long accountId, Long userId) {
        Account account = getAuthorizedAccount(accountId, userId);
        return new AccountResponse(account.getId(), account.getAccountNumber(), account.getBalance());
    }

    @Transactional
    public OperationResult  deposit(DepositRequest request) {
        Account account = accountRepository.findById(request.getAccountId())
            .orElseThrow(() -> new IllegalArgumentException("계좌를 찾을 수 없습니다."));

        OperationResult result = account.deposit(request.getAmount());
        if (!result.isSuccess()) {
            return result; // 실패 시 메시지 전달
        }

        Transaction tx = Transaction.builder()
            .type(TransactionType.DEPOSIT)
            .amount(request.getAmount())
            .senderName("외부 입금")
            .receiverName(account.getUser().getName())
            .balanceAfter(account.getBalance())
            .account(account)
            .build();

        transactionRepository.save(tx);

        return OperationResult.ok("입금 성공");
    }

    @Transactional
    public OperationResult withdraw(WithdrawRequest request) {
        Account account = accountRepository.findById(request.getAccountId())
            .orElseThrow(() -> new IllegalArgumentException("계좌를 찾을 수 없습니다."));

        OperationResult result = account.withdraw(request.getAmount());
        if (!result.isSuccess()) {
            return result;
        }

        Transaction tx = Transaction.builder()
            .type(TransactionType.WITHDRAW)
            .amount(request.getAmount())
            .senderName(account.getUser().getName())
            .receiverName("현금 출금")
            .balanceAfter(account.getBalance())
            .account(account)
            .build();

        transactionRepository.save(tx);

        return OperationResult.ok("출금 성공");
    }

    @Transactional
    public OperationResult transfer(TransferRequest request) {
        Account from = accountRepository.findById(request.getFromAccountId())
            .orElseThrow(() -> new IllegalArgumentException("출금 계좌를 찾을 수 없습니다."));

        Account to = accountRepository.findById(request.getToAccountId())
            .orElseThrow(() -> new IllegalArgumentException("입금 계좌를 찾을 수 없습니다."));

        OperationResult withdrawResult = from.withdraw(request.getAmount());
        if (!withdrawResult.isSuccess()) {
            return OperationResult.fail("이체 실패 - " + withdrawResult.getMessage());
        }

        OperationResult depositResult = to.deposit(request.getAmount());
        if (!depositResult.isSuccess()) {
            // 이론적으로 실패할 가능성은 낮지만, 롤백을 위해 throw 처리
            throw new IllegalStateException("이체 실패 - " + depositResult.getMessage());
        }

        from.withdraw(request.getAmount());
        to.deposit(request.getAmount());

        Transaction withdrawTx = Transaction.builder()
            .type(TransactionType.TRANSFER)
            .amount(request.getAmount())
            .senderName(from.getUser().getName())
            .receiverName(to.getUser().getName())
            .balanceAfter(from.getBalance())
            .account(from)
            .build();

        Transaction depositTx = Transaction.builder()
            .type(TransactionType.DEPOSIT)
            .amount(request.getAmount())
            .senderName(from.getUser().getName())
            .receiverName(to.getUser().getName())
            .balanceAfter(to.getBalance())
            .account(to)
            .build();

        transactionRepository.save(withdrawTx);
        transactionRepository.save(depositTx);

        return OperationResult.ok("이체 성공");
    }

    public List<TransactionResponse> getTransactions(Long accountId) {
        Account account = accountRepository.findById(accountId)
            .orElseThrow(() -> new IllegalArgumentException("계좌를 찾을 수 없습니다."));

        List<Transaction> transactions = transactionRepository.findByAccountIdOrderByCreatedAtDesc(accountId);

        return transactions.stream()
            .map(TransactionResponse::from)
            .collect(Collectors.toList());
    }

    private Account getAuthorizedAccount(Long accountId, Long userId) {
        Account account = accountRepository.findById(accountId)
            .orElseThrow(() -> new IllegalArgumentException("계좌를 찾을 수 없습니다."));

        if (!account.getUser().getId().equals(userId)) {
            throw new AccessDeniedException("해당 계좌에 대한 접근 권한이 없습니다.");
        }
        return account;
    }
}
