package org.example.bank.controller;

import org.example.bank.dto.request.CreateAccountRequest;
import org.example.bank.dto.request.DepositRequest;
import org.example.bank.dto.request.WithdrawRequest;
import org.example.bank.dto.response.AccountResponse;
import org.example.bank.service.AccountService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/accounts")
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @PostMapping
    public ResponseEntity<AccountResponse> createAccount(@RequestBody CreateAccountRequest request) {
        AccountResponse response = accountService.createAccount(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{accountId}")
    public ResponseEntity<AccountResponse> getAccount(@PathVariable Long accountId) {
        AccountResponse response = accountService.getAccount(accountId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/deposit")
    public ResponseEntity<AccountResponse> deposit(@RequestBody DepositRequest request) {
        AccountResponse response = accountService.deposit(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/withdraw")
    public ResponseEntity<AccountResponse> withdraw(@RequestBody WithdrawRequest request) {
        AccountResponse response = accountService.withdraw(request);
        return ResponseEntity.ok(response);
    }
}
