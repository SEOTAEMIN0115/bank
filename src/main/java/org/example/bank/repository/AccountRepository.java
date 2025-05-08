package org.example.bank.repository;

import java.util.Optional;

import jakarta.transaction.Transactional;
import org.example.bank.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;

public interface AccountRepository extends JpaRepository<Account, Long> {
    Optional<Account> findByAccountNumber(String accountNumber);

    @Modifying
    @Transactional
    void deleteAllByUserId(Long userId);
}
