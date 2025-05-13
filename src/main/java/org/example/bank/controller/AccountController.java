package org.example.bank.controller;

import java.time.LocalDate;
import java.util.List;

import lombok.RequiredArgsConstructor;
import org.example.bank.common.OperationResult;
import org.example.bank.dto.request.CreateAccountRequest;
import org.example.bank.dto.request.DepositRequest;
import org.example.bank.dto.request.TransferRequest;
import org.example.bank.dto.request.WithdrawRequest;
import org.example.bank.dto.response.AccountResponse;
import org.example.bank.dto.response.TransactionResponse;
import org.example.bank.entity.User;
import org.example.bank.service.AccountService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    @PostMapping
    public ResponseEntity<AccountResponse> createAccount(@RequestBody CreateAccountRequest request) {
        AccountResponse response = accountService.createAccount(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{accountId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<AccountResponse> getAccount(@PathVariable Long accountId) {
        User user = getCurrentUser();
        AccountResponse response = accountService.getAccount(accountId, user.getId());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/deposit")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> deposit(@RequestBody DepositRequest request) {
        OperationResult result = accountService.deposit(request);
        return buildResponse(result);
    }

    @PostMapping("/withdraw")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> withdraw(@RequestBody WithdrawRequest request) {
        OperationResult result = accountService.withdraw(request);
        return buildResponse(result);
    }

    @PostMapping("/transfer")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> transfer(@RequestBody TransferRequest request) {
        OperationResult result = accountService.transfer(request);
        return buildResponse(result);
    }

    @GetMapping("/{accountId}/transactions")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<List<TransactionResponse>> getTransactions(@PathVariable Long accountId) {
        List<TransactionResponse> responses = accountService.getTransactions(accountId);
        return ResponseEntity.ok(responses);
    }

    // 공통 응답 포맷
    private ResponseEntity<?> buildResponse(OperationResult result) {
        if (result.isSuccess()) {
            return ResponseEntity.ok(result.getMessage());
        } else {
            return ResponseEntity.badRequest().body(result.getMessage());
        }
    }

    // 현재 인증된 유저 가져오기
    private User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return (User) auth.getPrincipal();
    }

    @PostMapping("/{accountId}/deactivate")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> deactivateAccount(@PathVariable Long accountId) {
        User user = getCurrentUser();
        OperationResult result = accountService.deactivateAccount(accountId, user.getId());
        return buildResponse(result);
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/{accountId}/transactions/filter")
    public ResponseEntity<List<TransactionResponse>> filterTransactions(
        @PathVariable Long accountId,
        @RequestParam(required = false) String type,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {
        User user = getCurrentUser();
        List<TransactionResponse> responses = accountService.filterTransactions(accountId, user.getId(), type, startDate, endDate);
        return ResponseEntity.ok(responses);
    }
}