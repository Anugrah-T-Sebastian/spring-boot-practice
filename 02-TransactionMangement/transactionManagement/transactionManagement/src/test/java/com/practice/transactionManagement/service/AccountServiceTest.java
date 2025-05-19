package com.practice.transactionManagement.service;

import com.practice.transactionManagement.entity.Account;
import com.practice.transactionManagement.entity.Customer;
import com.practice.transactionManagement.exception.AccountNotFoundException;
import com.practice.transactionManagement.repository.AccountRepository;
import jakarta.inject.Inject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {
    @Mock
    private AccountRepository accountRepository;

    @Inject
    private AccountService accountService;

    private Customer testCustomer1;
    private Customer testCustomer2;
    private Account account1;
    private  Account account2;

    @BeforeEach
    public void init() {
        testCustomer1 = Customer.builder()
                .name("John Doe")
                .email("abc@abc.com")
                .build();

        testCustomer2 = Customer.builder()
                .name("Stephen King")
                .email("xyz@xyz.com")
                .build();

        account1 = Account.builder()
                .accountNumber(Long.valueOf("123456"))
                .accountType("SAVINGS")
                .balance(new BigDecimal(15000.00))
                .customerInfo(testCustomer1)
                .build();

        account2 = Account.builder()
                .accountNumber(Long.valueOf("988776"))
                .accountType("CHECKING")
                .balance(new BigDecimal(700.00))
                .customerInfo(testCustomer2)
                .build();
    }

    @Test
    void getAccountByAccountNumber_whenAccountExists_shouldReturnAccount() {
        // Given
        Long accountNumber = 123456L;

        when(accountRepository.findByIdWithLock(accountNumber))
                .thenReturn(Optional.of(account1));

        // When
        Account result = accountService.getAccountByAccountNumber(accountNumber);

        // Then
        assertNotNull(result);
        assertEquals(accountNumber, result.getAccountNumber());
        verify(accountRepository, times(1)).findByIdWithLock(accountNumber);
    }

    @Test
    void getAccountByAccountNumber_whenAccountNotExists_shouldThrowException() {
        // Given
        Long accountNumber = 45637486L;
        when(accountRepository.findByIdWithLock(accountNumber)).thenReturn(Optional.empty());

        // Then
        assertThrows(AccountNotFoundException.class, () -> {
            accountService.getAccountByAccountNumber(accountNumber);
        });
        verify(accountRepository, times(1)).findByIdWithLock(accountNumber);
    }

    @Test
    void creditToAccount_shouldIncreaseBalance() {
        // Given
        BigDecimal initialBalance = account1.getBalance();
        BigDecimal creditAmount = BigDecimal.valueOf(500);
        BigDecimal expectedBalance = initialBalance.add(creditAmount);

        // When
        accountService.creditToAccount(account1, creditAmount);

        // Then
        assertEquals(expectedBalance,account1.getBalance());
        verify(accountRepository, times(1)).save(account1);

    }

    @Test
    void creditToAccount_withZeroAmount_shouldNotChangeBalance() {
        // Given
        BigDecimal initialBalance = account1.getBalance();
        BigDecimal creditAmount = BigDecimal.ZERO;

        // When
        accountService.creditToAccount(account1, creditAmount);

        // Then
        assertEquals(initialBalance,account1.getBalance());
        verify(accountRepository, times(1)).save(account1);

    }

    @Test
    void debitFromAccount_shouldDecreaseBalance() {
        // Given
        BigDecimal intialBalance = account1.getBalance();
        BigDecimal debitAmount = BigDecimal.valueOf(150);
        BigDecimal expectedBalance = intialBalance.subtract(debitAmount);

        // When
        accountService.debitFromAccount(account1, debitAmount);

        // Then
        assertEquals(expectedBalance, account1.getBalance());
        verify(accountRepository, times(1)).save(account1);
    }

    @Test
    void debitFromAccount_withZeroAmount_shouldNotChangeBalance() {
        // Given
        BigDecimal intialBalance = account1.getBalance();
        BigDecimal debitAmount = BigDecimal.ZERO;

        // When
        accountService.debitFromAccount(account1, debitAmount);

        // Then
        assertEquals(intialBalance, account1.getBalance());
        verify(accountRepository, times(1)).save(account1);
    }

    @Test
    void debitFromAccount_withAmountEqualToBalance_shouldZeroBalance() {
        // Given
        BigDecimal intialBalance = account1.getBalance();
        BigDecimal debitAmount = account1.getBalance();

        // When
        accountService.debitFromAccount(account1, debitAmount);

        // Then
        assertEquals(BigDecimal.ZERO, account1.getBalance());
        verify(accountRepository, times(1)).save(account1);
    }

    @Test
    void debitFromAccount_withNegativeAmount_shouldThrowException() {
        // Given
        BigDecimal debitAmount = BigDecimal.valueOf(-1000);

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            accountService.debitFromAccount(account1, debitAmount);
        });
        verify(accountRepository, never()).save(account1);
    }

    @Test
    void debitFromAccount_withAmountGreaterThanBalance_shouldThrowException() {
        // Given
        BigDecimal initialBalance = account1.getBalance();
        BigDecimal debitAmount = initialBalance.add(BigDecimal.valueOf(100));

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            accountService.debitFromAccount(account1, debitAmount);
        });
        verify(accountRepository, never()).save(account1);
    }
}