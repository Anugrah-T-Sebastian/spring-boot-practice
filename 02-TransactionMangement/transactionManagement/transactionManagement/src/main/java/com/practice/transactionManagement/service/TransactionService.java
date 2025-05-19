package com.practice.transactionManagement.service;

import com.practice.transactionManagement.entity.Account;
import com.practice.transactionManagement.entity.Transaction;
import com.practice.transactionManagement.exception.AccountNotFoundException;
import com.practice.transactionManagement.exception.InsufficientFundsException;
import com.practice.transactionManagement.exception.InvalidAmountException;
import com.practice.transactionManagement.repository.TransactionRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@Service
public class TransactionService {
    private final TransactionRepository transactionRepository;
    private final AccountService accountService;

    @Transactional(rollbackOn = {AccountNotFoundException.class, InsufficientFundsException.class})
    public Transaction transferFunds(Long sourceAccountNum, Long destAccountNum, BigDecimal amount, String description) {
        // valid amount
        if (amount.compareTo(BigDecimal.ZERO) <= 0)
            throw new InvalidAmountException("Negative amount for transaction");

        // Get accounts with optimistic locking
        Account sourceAccount = accountService.getAccountByAccountNumber(sourceAccountNum);
        Account destAccount = accountService.getAccountByAccountNumber(destAccountNum);

        // Check sufficient balance
        if (sourceAccount.getBalance().compareTo(amount) < 0)
            throw new InsufficientFundsException("Insufficient balance");

        // Perform transfer
        accountService.debitFromAccount(sourceAccount, amount);
        accountService.creditToAccount(destAccount, amount);

        // Create transaction record
        Transaction transaction = Transaction.builder()
            .sourceAccount(sourceAccount)
            .destinationAccount(destAccount)
            .amount(amount)
            .timestamp(LocalDateTime.now())
            .status(Transaction.TransactionStatus.COMPLETED)
            .description(description)
            .build();
        transactionRepository.save(transaction);
        return transaction;
    }

    public List<Transaction> getAccountStatement(Long accountNumber, LocalDate fromDate, LocalDate toDate) {
        accountService.getAccountByAccountNumber(accountNumber);
        LocalDateTime startDateTime = fromDate.atStartOfDay();
        LocalDateTime endDateTime = toDate.atTime(23, 59, 59);

        List<Transaction> transactions = transactionRepository.findTransactionForAccountNumberBetweenDates(accountNumber, startDateTime, endDateTime);
        return transactions;
    }
}
