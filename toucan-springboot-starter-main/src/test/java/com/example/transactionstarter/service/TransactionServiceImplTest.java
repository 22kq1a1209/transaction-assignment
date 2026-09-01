package com.example.transactionstarter.service;

import com.example.transactionstarter.DTO.CreateTransactionRequest;
import com.example.transactionstarter.DTO.TransactionResponse;
import com.example.transactionstarter.DTO.UpdateTransactionStatusRequest;
import com.example.transactionstarter.entity.Transaction;
import com.example.transactionstarter.entity.TransactionStatus;
import com.example.transactionstarter.entity.TransactionType;
import com.example.transactionstarter.exception.DuplicateTransactionException;
import com.example.transactionstarter.exception.InvalidTransactionException;
import com.example.transactionstarter.exception.TransactionNotFoundException;
import com.example.transactionstarter.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.mock;

class TransactionServiceImplTest {

    private TransactionRepository transactionRepository;
    private TransactionServiceImpl transactionService;

    @BeforeEach
    void setUp() {
        transactionRepository = mock(TransactionRepository.class);
        transactionService = new TransactionServiceImpl(transactionRepository);
    }

    @Test
    void createsValidTransactionWithPendingStatus() {
        CreateTransactionRequest request = validCreateRequest();
        when(transactionRepository.existsById("TXN-1001")).thenReturn(false);
        when(transactionRepository.save(any(Transaction.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        TransactionResponse response = transactionService.createTransaction(request);

        assertEquals("TXN-1001", response.getTransactionId());
        assertEquals("CUST-2001", response.getCustomerId());
        assertEquals(new BigDecimal("1250.50"), response.getAmount());
        assertEquals(TransactionType.PAYMENT, response.getTransactionType());
        assertEquals(TransactionStatus.PENDING, response.getStatus());
        verify(transactionRepository).save(any(Transaction.class));
    }

    @Test
    void rejectsTransactionWithZeroAmount() {
        CreateTransactionRequest request = validCreateRequest();
        request.setAmount(BigDecimal.ZERO);

        InvalidTransactionException exception = assertThrows(
                InvalidTransactionException.class,
                () -> transactionService.createTransaction(request)
        );

        assertEquals("Amount must be greater than zero", exception.getMessage());
        verify(transactionRepository, never()).save(any(Transaction.class));
    }

    @Test
    void rejectsDuplicateTransactionId() {
        CreateTransactionRequest request = validCreateRequest();
        when(transactionRepository.existsById("TXN-1001")).thenReturn(true);

        DuplicateTransactionException exception = assertThrows(
                DuplicateTransactionException.class,
                () -> transactionService.createTransaction(request)
        );

        assertEquals("Transaction with ID TXN-1001 already exists", exception.getMessage());
        verify(transactionRepository, never()).save(any(Transaction.class));
    }

    @Test
    void reportsMissingTransaction() {
        when(transactionRepository.findById("UNKNOWN")).thenReturn(Optional.empty());

        TransactionNotFoundException exception = assertThrows(
                TransactionNotFoundException.class,
                () -> transactionService.getTransaction("UNKNOWN")
        );

        assertEquals("Transaction with ID UNKNOWN not found", exception.getMessage());
    }

    @Test
    void updatesPendingTransactionToCompleted() {
        Transaction transaction = new Transaction(
                "TXN-1001",
                "CUST-2001",
                new BigDecimal("1250.50"),
                "INR",
                TransactionType.PAYMENT,
                TransactionStatus.PENDING
        );
        UpdateTransactionStatusRequest request = new UpdateTransactionStatusRequest();
        request.setStatus(TransactionStatus.COMPLETED);

        when(transactionRepository.findById("TXN-1001")).thenReturn(Optional.of(transaction));
        when(transactionRepository.save(transaction)).thenReturn(transaction);

        TransactionResponse response = transactionService.updateTransactionStatus("TXN-1001", request);

        assertEquals(TransactionStatus.COMPLETED, response.getStatus());
        verify(transactionRepository).save(transaction);
    }

    private CreateTransactionRequest validCreateRequest() {
        CreateTransactionRequest request = new CreateTransactionRequest();
        request.setTransactionId("TXN-1001");
        request.setCustomerId("CUST-2001");
        request.setAmount(new BigDecimal("1250.50"));
        request.setCurrency("INR");
        request.setTransactionType(TransactionType.PAYMENT);
        return request;
    }
}
