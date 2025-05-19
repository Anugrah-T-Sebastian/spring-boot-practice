package com.practice.transactionManagement.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.List;

@Builder
@Getter
@Setter
@Entity
@Table(name = "customer")
public class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long customerId;

    @Column(nullable = false)
    private String name;

    @Column(unique = true, nullable = false)
    private String email;
    private String phone;
    private String address;

    @OneToMany(
            mappedBy ="customerInfo",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    List<Account> accounts;
}
