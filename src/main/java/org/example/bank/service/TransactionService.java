package org.example.bank.service;

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
            .map(t -> new TransactionResponse(
                t.getType(),
                t.getAmount(),
                t.getBalanceAfter(),
                t.getCreatedAt()))
            .collect(Collectors.toList());
    }
}
