package org.example.bank.controller;

import java.time.LocalDate;
import java.util.List;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.example.bank.dto.response.TransactionResponse;
import org.example.bank.service.TransactionService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Transaction", description = "거래 관련 API")
@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    @GetMapping("/users/{userId}")
    public ResponseEntity<List<TransactionResponse>> getUserTransactionsByPeriod(
            @PathVariable Long userId,
            @RequestParam("from") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam("to") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {

        List<TransactionResponse> responses = transactionService.getTransactionsByPeriod(userId, from, to);
        return ResponseEntity.ok(responses);
    }

    @Operation(summary = "사용자 거래 내역 조회", description = "해당 사용자의 전체 입출금 및 이체 거래 내역을 조회합니다.")
    @GetMapping("/users/{userId}/transactions")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<List<TransactionResponse>> getUserTransactions(@PathVariable Long userId) {
        List<TransactionResponse> response = transactionService.getTransactionsByUser(userId);
        return ResponseEntity.ok(response);
    }
}
