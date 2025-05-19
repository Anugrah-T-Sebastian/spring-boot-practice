package com.practice.transactionManagement.controller;

import com.practice.transactionManagement.dto.ApiResponse;
import com.practice.transactionManagement.dto.ProcessFundTransferRequestDto;
import com.practice.transactionManagement.dto.TransactionResponse;
import com.practice.transactionManagement.entity.Transaction;
import com.practice.transactionManagement.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Validated
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class TransactionController {
    private final TransactionService transactionService;

    @PostMapping("/transfers")
    public ResponseEntity<ApiResponse<TransactionResponse>> processFundTransfer(@RequestBody ProcessFundTransferRequestDto request) {
        Transaction transaction = transactionService.transferFunds(
                Long.parseLong(request.getSourceAccount()),
                Long.parseLong(request.getDestinationAccount()),
                request.getAmount(),
                request.getDescription()
        );
        return ResponseEntity.ok(
                ApiResponse.<TransactionResponse>builder()
                        .success(true)
                        .data(mapToTransactionResponse(transaction))
                        .build()
        );
    }

    private TransactionResponse mapToTransactionResponse(Transaction transaction) {
        return null;
    }


    @GetMapping("/accounts/{accountNumber}/statement")
    public ResponseEntity<?> getAccountStatement(
            @PathVariable Long accountNumber,
            @RequestParam LocalDate fromDate,
            @PathVariable LocalDate toDate
    ) {
        List<Transaction> accountStatement = transactionService.getAccountStatement(accountNumber, fromDate, toDate);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(accountStatement);
    }
}
