package com.practice.transactionManagement.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
@Getter
@Setter
@Entity
@Table(name = "transaction")
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long transactionId;

    @Column(precision = 19, scale = 4)
    private BigDecimal amount;
    private LocalDateTime timestamp;

    @ManyToOne
    @JoinColumn(name = "sourceAccount")
    private Account sourceAccount;

    @ManyToOne
    @JoinColumn(name = "destinationAccount")
    private Account destinationAccount;

    @Enumerated
    private TransactionStatus status;
    private String description;

    public enum TransactionStatus {
        PENDING, COMPLETED, FAILED, REVERSED
    }

}
