package com.practice.transactionManagement.repository;

import com.practice.transactionManagement.entity.Account;
import com.practice.transactionManagement.entity.Customer;
import com.practice.transactionManagement.entity.Transaction;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
class TransactionRepositoryTest {

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private TransactionRepository transactionRepository;

    Customer customer1;
    Account account1;
    Account account2;
    Transaction transaction1;
    Transaction transaction2;
    Transaction transaction3;

    @BeforeEach
    void init() {
        customer1 = Customer.builder()
                .name("John Doe")
                .email("abc@abc.com")
                .build();
        entityManager.persist(customer1);

        account1 = Account.builder()
                .accountType("SAVING")
                .balance(new BigDecimal("1500.00"))
                .customerInfo(customer1)
                .build();
        account2 = Account.builder()
                .accountType("CHECKING")
                .balance(new BigDecimal("700.00"))
                .customerInfo(customer1)
                .build();
        entityManager.persist(account1);
        entityManager.persist(account2);

        transaction1 = Transaction.builder()
                .amount(new BigDecimal("300.00"))
                .timestamp(LocalDateTime.now().plusMinutes(15))
                .sourceAccount(account1)
                .destinationAccount(account2)
                .status(Transaction.TransactionStatus.COMPLETED)
                .build();
        transaction2 = Transaction.builder()
                .amount(new BigDecimal("400.00"))
                .timestamp(LocalDateTime.now().plusHours(6))
                .sourceAccount(account2)
                .destinationAccount(account1)
                .status(Transaction.TransactionStatus.COMPLETED)
                .build();
        entityManager.persist(transaction1);
        entityManager.persist(transaction2);

        entityManager.flush();
    }

    @Test
    void whenFindBySourceAccountOrDestinationAccount_thenReturnTra() {
        List<Transaction> foundTransactions = transactionRepository.findBySourceAccountOrDestinationAccount(account1);

        assertEquals(2, foundTransactions.size());
        assertThat(foundTransactions).extracting(Transaction::getTransactionId)
                .contains(transaction1.getTransactionId(), transaction2.getTransactionId());
    }

    @Test
    void whenFindByTimestampBetween_thenReturnTransactionsInDateRange() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime later = LocalDateTime.now().plusHours(10);

        List<Transaction> foudTransaction = transactionRepository.findByTimestampBetween(now, later);

        assertThat(foudTransaction).hasSize(2);
        assertThat(foudTransaction).extracting(Transaction::getTransactionId)
                .contains(transaction1.getTransactionId(), transaction2.getTransactionId());
    }

    @Test
    void whenFindByStatus_thenReturnTransactionsWithStatus() {
        List<Transaction> foundTransaction = transactionRepository.findByStatus(Transaction.TransactionStatus.COMPLETED);

        assertThat(foundTransaction).hasSize(2);
        assertThat(foundTransaction).extracting(Transaction::getTransactionId)
                .contains(transaction1.getTransactionId(), transaction2.getTransactionId());
    }

    @Test
    void whenFindByStatus_thenReturnNoTransactionsWithStatus() {
        List<Transaction> foundTransaction = transactionRepository.findByStatus(Transaction.TransactionStatus.FAILED);

        assertThat(foundTransaction).isEmpty();
    }

}