package org.example.bank.dto.response;

import java.time.LocalDateTime;

import org.example.bank.entity.Transaction;
import org.example.bank.entity.TransactionType;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TransactionResponse {

    private Long id;
    private String type;
    private Long amount;
    private String senderName;
    private String receiverName;
    private LocalDateTime createdAt;

    public static TransactionResponse from(Transaction tx) {
        return new TransactionResponse(
                tx.getId(),
                tx.getType().name(),
                tx.getAmount(),
                tx.getSender() != null ? tx.getSender().getName() : null,
                tx.getReceiver() != null ? tx.getReceiver().getName() : null,
                tx.getCreatedAt()
        );
    }
}