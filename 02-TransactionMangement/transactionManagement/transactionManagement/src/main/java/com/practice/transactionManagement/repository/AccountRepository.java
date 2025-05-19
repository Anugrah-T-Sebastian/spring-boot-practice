package com.practice.transactionManagement.repository;

import com.practice.transactionManagement.entity.Account;
import com.practice.transactionManagement.entity.Customer;
import jakarta.persistence.LockModeType;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public  interface AccountRepository extends JpaRepository<Account, Long> {

    List<Account> findByBalanceGreaterThan(BigDecimal balance);

    List<Account> findByCustomerInfo(Customer customer);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT a FROM Account a where a.accountNumber = :accountNum")
    public Optional<Account> findByIdWithLock(@Param("accountNum") Long accountNum);
}
