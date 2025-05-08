package org.example.bank.repository;

import java.util.List;

import org.example.bank.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findByAccountIdOrderByCreatedAtDesc(Long accountId);

    @Query("""
    SELECT t FROM Transaction t
    WHERE t.sender.id = :userId OR t.receiver.id = :userId
    ORDER BY t.createdAt DESC
    """)
    List<Transaction> findAllByUserId(@Param("userId") Long userId);
}
