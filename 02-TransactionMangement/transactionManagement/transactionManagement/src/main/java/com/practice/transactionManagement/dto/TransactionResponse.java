package com.practice.transactionManagement.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;

@Builder
@Getter
@Setter
public class TransactionResponse {
    private String tranasctionId;
    private String status;
    private BigDecimal amount;
    private String description;
    private Instant timestamp;
}
