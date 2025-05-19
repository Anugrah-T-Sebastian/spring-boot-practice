package com.practice.transactionManagement.service;

import com.practice.transactionManagement.entity.Account;
import com.practice.transactionManagement.entity.Customer;
import com.practice.transactionManagement.entity.Transaction;
import com.practice.transactionManagement.exception.AccountNotFoundException;
import com.practice.transactionManagement.exception.InsufficientFundsException;
import com.practice.transactionManagement.exception.InvalidAmountException;
import com.practice.transactionManagement.repository.TransactionRepository;
import jakarta.inject.Inject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private AccountService accountService;

    @Inject
    private  TransactionService transactionService;

    private Customer testCustomer1;
    private Customer testCustomer2;
    private Long sourceAccountNum = 123456L;
    private Long destAccountNum = 987456L;
    private Account sourceAccount;
    private  Account destAccount;
    private LocalDate fromDate = LocalDate.now().minusDays(7);
    private LocalDate toDate = LocalDate.now().plusDays(7);


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

        sourceAccount = Account.builder()
                .accountNumber(sourceAccountNum)
                .accountType("SAVINGS")
                .balance( BigDecimal.valueOf(15000.00))
                .customerInfo(testCustomer1)
                .build();

        destAccount = Account.builder()
                .accountNumber(destAccountNum)
                .accountType("CHECKING")
                .balance(BigDecimal.valueOf(700.00))
                .customerInfo(testCustomer2)
                .build();
    }

    @Test
    void transferFunds_withValidData_shouldCompleteTransaction() {
        // Given
        BigDecimal amount = BigDecimal.valueOf(100);
        String description = "Funds transfered";
        when(accountService.getAccountByAccountNumber(sourceAccountNum)).thenReturn(sourceAccount);
        when(accountService.getAccountByAccountNumber(destAccountNum)).thenReturn(destAccount);
        when(transactionRepository.save(any(Transaction.class))).then(invocationOnMock -> invocationOnMock.getArgument(0));

        // When
        Transaction result = transactionService.transferFunds(sourceAccountNum, destAccountNum, amount, description);

        // Then
        assertNotNull(result);
        assertEquals(sourceAccount, result.getSourceAccount());
        assertEquals(destAccountNum, result.getDestinationAccount());
        assertEquals(amount, result.getAmount());
        assertEquals(Transaction.TransactionStatus.COMPLETED, result.getStatus());
        assertEquals(description, result.getDescription());

        verify(accountService).debitFromAccount(sourceAccount, amount);
        verify(accountService).creditToAccount(destAccount, amount);
        verify(transactionRepository, times(1)).save(result);
    }

    @Test
    void transferFunds_withZeroAmount_shouldThrowInvalidAmountException() {
        BigDecimal amount = BigDecimal.ZERO;
        String description = "Zero transfer";

        //When & Then
        assertThrows(InvalidAmountException.class, () -> {
            transactionService.transferFunds(
                    sourceAccountNum,
                    destAccountNum,
                    amount,
                    description
            );
        });

        verifyNoInteractions(accountService);
        verifyNoInteractions(transactionRepository);
    }

    @Test
    void transferFunds_withInsufficientFunds_shouldThrowInsufficientFundsException() {
        BigDecimal amount = BigDecimal.valueOf(10000);
        String description = "Insufficient fund transfer";

        when(accountService.getAccountByAccountNumber(sourceAccountNum))
                .thenReturn(sourceAccount);
        when(accountService.getAccountByAccountNumber(destAccountNum))
                .thenReturn(destAccount);

        // When & Then
        assertThrows(InsufficientFundsException.class, () -> {
            transactionService.transferFunds(
                    sourceAccountNum,
                    destAccountNum,
                    amount,
                    description
            );
        });

        verify(accountService, never()).debitFromAccount(any(), any());
        verify(accountService, never()).creditToAccount(any(), any());
        verify(transactionRepository, never()).save(any());
    }

    @Test
    void transferFunds_withNonExistentSourceAccount_shouldThrowAccountNotFoundException() {
        // Given
        Long incorrectSourceAccountNum = 45L;
        BigDecimal amount = BigDecimal.valueOf(500);
        String description = "Non-existent source";

        when(accountService.getAccountByAccountNumber(incorrectSourceAccountNum))
                .thenThrow(new AccountNotFoundException("Account not found"));

        // When & Then
        assertThrows(AccountNotFoundException.class, () -> {
            transactionService.transferFunds(
                    incorrectSourceAccountNum,
                    destAccountNum,
                    amount,
                    description
            );
        });

        verify(accountService, never()).getAccountByAccountNumber(destAccountNum);
        verify(accountService, never()).debitFromAccount(any(), any());
        verify(accountService, never()).creditToAccount(any(), any());
        verify(transactionRepository, never()).save(any());
    }

    @Test
    void getAccountStatement_withValidData_shouldReturnTransactions() {
        //Given
        Transaction transaction1 = Transaction.builder()
                .amount(new BigDecimal("300.00"))
                .timestamp(LocalDateTime.now().plusMinutes(15))
                .sourceAccount(sourceAccount)
                .destinationAccount(destAccount)
                .status(Transaction.TransactionStatus.COMPLETED)
                .build();
        Transaction transaction2 = Transaction.builder()
                .amount(new BigDecimal("400.00"))
                .timestamp(LocalDateTime.now().plusHours(6))
                .sourceAccount(sourceAccount)
                .destinationAccount(destAccount)
                .status(Transaction.TransactionStatus.COMPLETED)
                .build();
        List<Transaction> expectTransactions = List.of(transaction1, transaction2);

        when(accountService.getAccountByAccountNumber(sourceAccountNum))
                .thenReturn(sourceAccount);
        when(transactionRepository.findTransactionForAccountNumberBetweenDates(
                sourceAccountNum,
                fromDate.atStartOfDay(),
                toDate.atTime(23, 59, 59)
        ));

        // When
        List<Transaction> accountStatement = transactionService.getAccountStatement(
                sourceAccountNum,
                fromDate,
                toDate
        );

        // Then
        assertNotNull(accountStatement);
        assertEquals(expectTransactions.size(), accountStatement.size());
        assertEquals(expectTransactions, accountStatement);
        verify(accountService).getAccountByAccountNumber(sourceAccountNum);
    }

    @Test
    void getAccountStatement_withNonExistentAccount_shouldThrowAccountNotFoundException() {
        Long incorrectSrcAccountNum = 00L;
        when(accountService.getAccountByAccountNumber(incorrectSrcAccountNum))
                .thenThrow(new AccountNotFoundException("Account not found"));

        // When & Then
        assertThrows(AccountNotFoundException.class, () -> {
            transactionService.getAccountStatement(
                    sourceAccountNum,
                    fromDate,
                    toDate
            );
        });
        verify(transactionRepository, never()).findTransactionForAccountNumberBetweenDates(
                any(),
                any(),
                any()
        );
    }

    @Test
    void getAccountStatement_withInvalidDateRange_shouldReturnEmptyList() {
        LocalDate invalidFromDate = LocalDate.now().plusDays(2);
        LocalDate invalidToDate = LocalDate.now();

        when(accountService.getAccountByAccountNumber(sourceAccountNum))
                .thenReturn(sourceAccount);
        when(transactionRepository.findTransactionForAccountNumberBetweenDates(
                eq(sourceAccountNum),
                any(LocalDateTime.class),
                any(LocalDateTime.class)
        )).thenReturn(List.of());

        //When
        List<Transaction> accountStatement = transactionService.getAccountStatement(
                sourceAccountNum,
                fromDate,
                toDate
        );

        // Then
        assertTrue(accountStatement.isEmpty());
    }
}