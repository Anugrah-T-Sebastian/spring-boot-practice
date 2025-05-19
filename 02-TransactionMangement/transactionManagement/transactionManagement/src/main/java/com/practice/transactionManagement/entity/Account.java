package com.practice.transactionManagement.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;


@Builder
@Getter
@Setter
@Entity
@Table(name = "account")
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long accountNumber;

    private String accountType;

    @Column(precision = 19, scale = 4)
    private BigDecimal balance;

    private LocalDate openingDate;

    @ManyToOne
    @JoinColumn(name = "customerInfo")
    private Customer customerInfo;

    @OneToMany(
            mappedBy = "sourceAccount",
            cascade = {CascadeType.PERSIST, CascadeType.MERGE},
            orphanRemoval = false
    )
    List<Transaction> debitTransaction;

    @OneToMany(
            mappedBy = "destinationAccount",
            cascade = {CascadeType.PERSIST, CascadeType.MERGE},
            orphanRemoval = false
    )
    List<Transaction> creditTransaction;
}
