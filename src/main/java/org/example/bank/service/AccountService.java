package org.example.bank.service;


import lombok.RequiredArgsConstructor;
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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;

    @Transactional
    public AccountResponse createAccount(CreateAccountRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        Account account = new Account(user);
        accountRepository.save(account);

        return AccountResponse.fromEntity(account);
    }

    @Transactional
    public OperationResult deposit(DepositRequest request) {
        Account account = accountRepository.findById(request.getAccountId())
                .orElseThrow(() -> new IllegalArgumentException("계좌를 찾을 수 없습니다."));

        if (!account.isActive()) {
            return OperationResult.fail("비활성화된 계좌에서는 출금할 수 없습니다.");
        }

        OperationResult result = account.deposit(request.getAmount());
        if (!result.isSuccess()) {
            return result;
        }

        Transaction tx = Transaction.builder()
                .type(TransactionType.DEPOSIT)
                .amount(request.getAmount())
                .sender(null)
                .receiver(account.getUser())
                .account(account)
                .build();

        transactionRepository.save(tx);
        return OperationResult.ok("입금 성공");
    }

    @Transactional
    public OperationResult withdraw(WithdrawRequest request) {
        Account account = accountRepository.findById(request.getAccountId())
                .orElseThrow(() -> new IllegalArgumentException("계좌를 찾을 수 없습니다."));

        if (!account.isActive()) {
            return OperationResult.fail("비활성화된 계좌에서는 출금할 수 없습니다.");
        }

        OperationResult result = account.withdraw(request.getAmount());
        if (!result.isSuccess()) {
            return result;
        }

        Transaction tx = Transaction.builder()
                .type(TransactionType.WITHDRAW)
                .amount(request.getAmount())
                .sender(account.getUser())
                .receiver(null)
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

        if (!from.isActive() || !to.isActive()) {
            return OperationResult.fail("비활성화된 계좌와의 거래는 불가능합니다.");
        }

        OperationResult withdrawResult = from.withdraw(request.getAmount());

        if (!withdrawResult.isSuccess()) {
            return OperationResult.fail("이체 실패 - " + withdrawResult.getMessage());
        }

        OperationResult depositResult = to.deposit(request.getAmount());
        if (!depositResult.isSuccess()) {
            throw new IllegalStateException("이체 실패 - " + depositResult.getMessage());
        }

        Transaction withdrawTx = Transaction.builder()
                .type(TransactionType.TRANSFER)
                .amount(request.getAmount())
                .sender(from.getUser())
                .receiver(to.getUser())
                .account(from)
                .build();

        Transaction depositTx = Transaction.builder()
                .type(TransactionType.DEPOSIT)
                .amount(request.getAmount())
                .sender(from.getUser())
                .receiver(to.getUser())
                .account(to)
                .build();

        transactionRepository.save(withdrawTx);
        transactionRepository.save(depositTx);

        return OperationResult.ok("이체 성공");
    }

    public List<TransactionResponse> getTransactionsByAccount(Long accountId) {
        List<Transaction> transactions = transactionRepository.findByAccountIdOrderByCreatedAtDesc(accountId);
        return transactions.stream()
                .map(TransactionResponse::from)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<TransactionResponse> getTransactions(Long accountId) {

        Account account = accountRepository.findById(accountId)
            .orElseThrow(() -> new IllegalArgumentException("계좌를 찾을 수 없습니다."));

        if (!account.isActive()) {
            throw new IllegalStateException("비활성화된 계좌의 거래내역은 조회할 수 없습니다.");
        }
        // accountId로 거래내역 조회
        List<Transaction> transactions = transactionRepository.findByAccountIdOrderByCreatedAtDesc(accountId);

        // Transaction -> TransactionResponse로 변환
        return transactions.stream()
                .map(TransactionResponse::from)
                .collect(Collectors.toList());
    }

    public AccountResponse getAccount(Long accountId, Long userId) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new IllegalArgumentException("계좌를 찾을 수 없습니다."));

        if (!account.getUser().getId().equals(userId)) {
            throw new SecurityException("해당 계좌에 접근할 권한이 없습니다.");
        }

        return AccountResponse.fromEntity(account);
    }

    @Transactional
    public OperationResult deactivateAccount(Long accountId, Long userId) {
        Account account = accountRepository.findById(accountId)
            .orElseThrow(() -> new IllegalArgumentException("계좌를 찾을 수 없습니다."));

        if (!account.getUser().getId().equals(userId)) {
            return OperationResult.fail("본인의 계좌만 비활성화할 수 있습니다.");
        }

        if (!account.isActive()) {
            return OperationResult.fail("이미 비활성화된 계좌입니다.");
        }

        account.deactivate();
        return OperationResult.ok("계좌가 성공적으로 비활성화되었습니다.");
    }

    public List<TransactionResponse> filterTransactions(Long accountId, Long userId, String type, LocalDate start, LocalDate end) {
        // 계좌 소유자 검증 후
        List<Transaction> filtered = transactionRepository.findByFilters(accountId, type, start, end);
        return filtered.stream().map(TransactionResponse::from).toList();
    }
}
