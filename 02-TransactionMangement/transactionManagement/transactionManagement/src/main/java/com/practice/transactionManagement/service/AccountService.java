package com.practice.transactionManagement.service;

import com.practice.transactionManagement.entity.Account;
import com.practice.transactionManagement.exception.AccountNotFoundException;
import com.practice.transactionManagement.repository.AccountRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class AccountService {
    private final AccountRepository accountRepository;

    Account getAccountByAccountNumber(Long accountNum) {
        return accountRepository.findByIdWithLock(accountNum)
                .orElseThrow(() -> new AccountNotFoundException("Account with account number: " + accountNum + " does not exists"));
    }

    @Transactional
    void creditToAccount(Account account, BigDecimal amount) {
        account.setBalance(account.getBalance().add(amount));
        accountRepository.save(account);
    }

    @Transactional
    void debitFromAccount(Account account, BigDecimal amount) {
        account.setBalance(account.getBalance().subtract(amount));
        accountRepository.save(account);
    }
}
