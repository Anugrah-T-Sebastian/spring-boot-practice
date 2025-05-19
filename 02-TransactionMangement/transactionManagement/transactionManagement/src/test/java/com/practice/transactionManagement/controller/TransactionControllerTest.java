package com.practice.transactionManagement.controller;

import com.practice.transactionManagement.entity.Account;
import com.practice.transactionManagement.entity.Customer;
import com.practice.transactionManagement.entity.Transaction;
import com.practice.transactionManagement.exception.AccountNotFoundException;
import com.practice.transactionManagement.exception.InvalidAmountException;
import com.practice.transactionManagement.service.TransactionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class TransactionControllerTest {

    private MockMvc mockMvc;

    @Mock
    private TransactionService transactionService;

    @InjectMocks
    private TransactionController transactionController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(transactionController).build();
    }

    @Test
    void processFundTransfer_shouldReturnSuccessResponse() throws Exception {
        // Given
        Account sourceAccount = Account.builder()
                .accountNumber(123456L)
                .accountType("SAVING")
                .balance(BigDecimal.valueOf(1000))
                .build();
        Account destAccount = Account.builder()
                .accountNumber(789012L)
                .accountType("CURRENT")
                .balance(BigDecimal.valueOf(1000))
                .build();
        Transaction transaction = Transaction.builder()
                .transactionId(1L)
                .amount(new BigDecimal("300.00"))
                .timestamp(LocalDateTime.now().plusMinutes(15))
                .sourceAccount(sourceAccount)
                .destinationAccount(destAccount)
                .status(Transaction.TransactionStatus.COMPLETED)
                .build();

        when(transactionService.transferFunds(
                anyLong(),
                anyLong(),
                any(BigDecimal.class),
                any(String.class))
        ).thenReturn(transaction);

        // When & Then
        mockMvc.perform(post("/api/v1/transfers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                        {
                            "sourceAccount": "123456",
                            "destinationAccount": "789012",
                            "amount": 500,
                            "description": "Test transfer"
                        }
                        """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.transactionId", is(1)))
                .andExpect(jsonPath("$.data.amount", is(300.00)))
                .andExpect(jsonPath("$.data.status", is("COMPLETED")));
    }

    @Test
    void processFundTransfer_withInvalidAmount_shouldReturnBadRequest() throws Exception {
        // Given
        when(transactionService.transferFunds(
                anyLong(),
                anyLong(),
                any(BigDecimal.class),
                any(String.class)
        )).thenThrow(new InvalidAmountException("Negative amount for transaction"));

        //When & Then
        mockMvc.perform(post("api/v1/transfers")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                            "sourceAccount": "123456",
                            "destinationAccount": "789012",
                            "amount": -500,
                            "description": "Test transfer"
                        }
                        """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getAccountStatement_shouldReturnTransactions() throws Exception {
        Customer testCustomer1 = Customer.builder()
                .name("John Doe")
                .email("abc@abc.com")
                .build();

        Customer testCustomer2 = Customer.builder()
                .name("Stephen King")
                .email("xyz@xyz.com")
                .build();

        Long sourceAccountNum = 123456L;
        Long destAccountNum = 987456L;
        Account sourceAccount = Account.builder()
                .accountNumber(sourceAccountNum)
                .accountType("SAVINGS")
                .balance( BigDecimal.valueOf(15000.00))
                .customerInfo(testCustomer1)
                .build();

        Account destAccount = Account.builder()
                .accountNumber(destAccountNum)
                .accountType("CHECKING")
                .balance(BigDecimal.valueOf(700.00))
                .customerInfo(testCustomer2)
                .build();
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

        LocalDate fromDate = LocalDate.now().minusDays(7);
        LocalDate toDate = LocalDate.now().plusDays(7);

        when(transactionService.getAccountStatement(
                sourceAccountNum,
                fromDate,
                toDate
        )).thenReturn(expectTransactions);

        // When & Then
        mockMvc.perform(get("api/v1/account/{accountNumber}/statement", sourceAccount)
                .param("fromDate", fromDate.toString())
                .param("toDate", toDate.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].transactionId", is(transaction1.getTransactionId())))
                .andExpect(jsonPath("$[0].amount", is(transaction1.getAmount())))
                .andExpect(jsonPath("$[0].status", is(transaction1.getStatus())));
    }

    @Test
    void getAccountStatement_withNonExistentAccount_shouldReturnNotFound() throws Exception {
        Long invalidAccountNumber = 4515L;
        LocalDate fromDate = LocalDate.now().minusDays(7);
        LocalDate toDate = LocalDate.now().plusDays(7);
        when(transactionService.getAccountStatement(invalidAccountNumber, fromDate, toDate))
                .thenThrow(new AccountNotFoundException("Account not found"));

        //When & Then
        mockMvc.perform(get("/api/v1/accounts/{accountNumber}/statement", invalidAccountNumber)
                .param("fromDate", fromDate.toString())
                .param("toDate", toDate.toString()))
                .andExpect(status().isNotFound());
    }
}