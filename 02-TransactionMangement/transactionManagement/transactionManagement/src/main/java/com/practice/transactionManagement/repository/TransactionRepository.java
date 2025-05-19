package com.practice.transactionManagement.repository;

import com.practice.transactionManagement.entity.Account;
import com.practice.transactionManagement.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.awt.print.Pageable;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findBySourceAccountOrDestinationAccount(Account account);

    List<Transaction> findByTimestampBetween(LocalDateTime startDate, LocalDateTime endDate);

    List<Transaction> findByStatus(Transaction.TransactionStatus transactionStatus);

    @Query("SELECT t FROM Transaction t WHERE t.sourceAccount = :accountNumber OR t.destinationAccount = :accountNumber")
    List<Transaction> findTransactionForAccountNumber(@Param("accountNumber") Long accountNumber, Pageable pageable);

    @Query("""
            SELECT t FROM Transaction t 
            WHERE t.sourceAccount = :accountNumber 
            AND t.timestamp BETWEEN :startDateTime AND :endDateTime
            """)
    List<Transaction> findTransactionForAccountNumberBetweenDates(
            @Param("accountNumber") Long accountNumber,
            @Param("startDateTime") LocalDateTime startDateTime,
            @Param("endDateTime") LocalDateTime endDateTime);
}
