package com.practice.transactionManagement.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Builder
@Getter
@Setter
public class ProcessFundTransferRequestDto {
    private String sourceAccount;
    private String destinationAccount;
    private BigDecimal amount;
    private String description;
}
