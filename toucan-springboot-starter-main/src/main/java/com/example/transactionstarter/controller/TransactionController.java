package com.example.transactionstarter.controller;

import com.example.transactionstarter.DTO.CreateTransactionRequest;
import com.example.transactionstarter.DTO.TransactionResponse;
import com.example.transactionstarter.DTO.UpdateTransactionStatusRequest;
import com.example.transactionstarter.service.TransactionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    // 1. Create Transaction
    @PostMapping("/transactions")
    public ResponseEntity<TransactionResponse> createTransaction(
            @RequestBody CreateTransactionRequest request) {

        TransactionResponse response =
                transactionService.createTransaction(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    // 2. Get Transaction
    @GetMapping("/transactions/{transactionId}")
    public ResponseEntity<TransactionResponse> getTransaction(
            @PathVariable String transactionId) {

        TransactionResponse response =
                transactionService.getTransaction(transactionId);

        return ResponseEntity.ok(response);
    }

    // 3. Update Transaction Status
    @PatchMapping("/transactions/{transactionId}/status")
    public ResponseEntity<TransactionResponse> updateTransactionStatus(
            @PathVariable String transactionId,
            @RequestBody UpdateTransactionStatusRequest request) {

        TransactionResponse response =
                transactionService.updateTransactionStatus(
                        transactionId,
                        request
                );

        return ResponseEntity.ok(response);
    }

    // 4. Get Customer Transactions
    @GetMapping("/customers/{customerId}/transactions")
    public ResponseEntity<List<TransactionResponse>> getCustomerTransactions(
            @PathVariable String customerId) {

        List<TransactionResponse> response =
                transactionService.getCustomerTransactions(customerId);

        return ResponseEntity.ok(response);
    }
}