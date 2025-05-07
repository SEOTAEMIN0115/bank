package org.example.bank.entity;

import java.time.LocalDateTime;

import org.example.bank.common.OperationResult;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 20)
    private String accountNumber;

    @Column(nullable = false)
    private Long balance;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    public Account(String accountNumber, Long balance, User user) {
        this.accountNumber = accountNumber;
        this.balance = balance;
        this.user = user;
    }

    public OperationResult deposit(Long amount) {
        if (amount == null || amount <= 0) {
            return OperationResult.fail("입금 금액은 0보다 커야 합니다.");
        }

        this.balance += amount;
        return OperationResult.ok("입금 완료");
    }

    public OperationResult withdraw(Long amount) {
        if (amount == null || amount <= 0) {
            return OperationResult.fail("출금 금액은 0보다 커야 합니다.");
        }
        if (this.balance < amount) {
            return OperationResult.fail("잔액이 부족합니다.");
        }

        this.balance -= amount;
        return OperationResult.ok("출금 성공");
    }
}