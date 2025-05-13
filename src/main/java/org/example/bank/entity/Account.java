package org.example.bank.entity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import jakarta.persistence.*;
import org.example.bank.common.OperationResult;

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

    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL, orphanRemoval = true , fetch = FetchType.LAZY)
    private List<Transaction> transactions;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    private boolean isActive = true;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    public Account(User user) {
        this.accountNumber = UUID.randomUUID().toString().substring(0, 10); // 또는 고유 번호 생성 로직
        this.balance = 0L;
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

    public void deactivate() {
        this.isActive = false;
    }

    public boolean isActive() {
        return isActive;
    }
}