package org.example.bank.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.example.bank.dto.response.TransactionResponse;
import org.example.bank.entity.Transaction;
import org.example.bank.repository.TransactionRepository;
import org.springframework.stereotype.Service;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;

    public TransactionService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    public List<TransactionResponse> getTransactionsByAccount(Long accountId) {
        List<Transaction> transactions = transactionRepository.findByAccountIdOrderByCreatedAtDesc(accountId);

        return transactions.stream()
                .map(TransactionResponse::from)
                .collect(Collectors.toList());
    }

    public List<TransactionResponse> getTransactionsByPeriod(Long userId, LocalDate from, LocalDate to) {
        LocalDateTime start = from.atStartOfDay();
        LocalDateTime end = to.plusDays(1).atStartOfDay(); // inclusive

        List<Transaction> transactions = transactionRepository.findByUserInPeriod(userId, start, end);
        return transactions.stream()
                .map(TransactionResponse::from)
                .collect(Collectors.toList());
    }

    public List<TransactionResponse> getTransactionsByUser(Long userId) {
        List<Transaction> transactions = transactionRepository.findBySenderIdOrReceiverIdOrderByCreatedAtDesc(userId, userId);

        return transactions.stream()
                .map(TransactionResponse::from)
                .collect(Collectors.toList());
    }
}
