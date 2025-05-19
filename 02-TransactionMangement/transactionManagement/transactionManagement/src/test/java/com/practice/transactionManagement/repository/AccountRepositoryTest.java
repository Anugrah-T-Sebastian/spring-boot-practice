package com.practice.transactionManagement.repository;

import com.practice.transactionManagement.entity.Account;
import com.practice.transactionManagement.entity.Customer;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
class AccountRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private AccountRepository accountRepository;

    private Customer testCustomer1;
    private Customer testCustomer2;
    private Account account1;
    private  Account account2;

    @BeforeEach
    void setUp() {
        testCustomer1 = Customer.builder()
                .name("John Doe")
                .email("abc@abc.com")
                .build();
        entityManager.persist(testCustomer1);

        testCustomer2 = Customer.builder()
                .name("Stephen King")
                .email("xyz@xyz.com")
                .build();
        entityManager.persist(testCustomer1);

        account1 = Account.builder()
                .accountType("SAVINGS")
                .balance(new BigDecimal(15000.00))
                .customerInfo(testCustomer1)
                .build();
        entityManager.persist(account1);

        account2 = Account.builder()
                .accountType("CHECKING")
                .balance(new BigDecimal(700.00))
                .customerInfo(testCustomer2)
                .build();
        entityManager.persist(account2);

        entityManager.flush();
    }

    @Test
    void whenFindByBalanceGreaterThan_thenReturnAccounts() {
        List<Account> accounts = accountRepository.findByBalanceGreaterThan(new BigDecimal(800.00));

        assertEquals(1, accounts.size());
        assertEquals(account1.getAccountNumber(), accounts.get(0).getAccountType());
    }

    @Test
    void whenFindByCustomerInfo_thenReturnsAccounts() {
        List<Account> accounts = accountRepository.findByCustomerInfo(testCustomer1);

        assertEquals(1, accounts.size());
        assertTrue(accounts
                .stream()
                .anyMatch(account -> account.getAccountNumber().equals(account1.getAccountNumber())));
        assertFalse(accounts
                .stream()
                .anyMatch(account -> account.getAccountNumber().equals(account2.getAccountNumber())));
    }

    @Test
    void whenFindByIdWithLock_thenReturnAccount() {
        Optional<Account> foundAccount = accountRepository.findByIdWithLock(account1.getAccountNumber());

        assertTrue(foundAccount.isPresent());
        assertEquals(foundAccount.get().getAccountNumber(), account1.getAccountNumber());
    }

    @Test
    void whenSaveAccount_thenCanRetrieveIt() {
        Account newAccount = Account.builder()
                .accountType("INVESTMENT")
                .balance(new BigDecimal("4000.00"))
                .customerInfo(testCustomer2)
                .build();

        Account savedAccount = accountRepository.save(newAccount);
        Account retrievedAccount = entityManager.find(Account.class, savedAccount.getAccountNumber());

        assertEquals(savedAccount.getAccountType(), retrievedAccount.getAccountType());
        assertEquals("INVESTMENT", retrievedAccount.getAccountType());
    }
}