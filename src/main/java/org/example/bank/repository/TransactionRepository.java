package org.example.bank.repository;

import org.example.bank.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findByAccountIdOrderByCreatedAtDesc(Long accountId);

    @Query("""
            SELECT t FROM Transaction t
            WHERE t.sender.id = :userId OR t.receiver.id = :userId
            ORDER BY t.createdAt DESC
            """)
    List<Transaction> findAllByUserId(@Param("userId") Long userId);

    @Query("""
                SELECT t FROM Transaction t
                WHERE (t.sender.id = :userId OR t.receiver.id = :userId)
                AND t.createdAt BETWEEN :from AND :to
                ORDER BY t.createdAt DESC
            """)
    List<Transaction> findByUserInPeriod(
            @Param("userId") Long userId,
            @Param("from") LocalDateTime from,
            @Param("to") LocalDateTime to
    );

    List<Transaction> findBySenderIdOrReceiverIdOrderByCreatedAtDesc(Long senderId, Long receiverId);
}
