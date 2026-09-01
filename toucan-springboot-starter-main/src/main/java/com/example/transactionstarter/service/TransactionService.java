package com.example.transactionstarter.service;

import com.example.transactionstarter.DTO.CreateTransactionRequest;
import com.example.transactionstarter.DTO.TransactionResponse;
import com.example.transactionstarter.DTO.UpdateTransactionStatusRequest;

import java.util.List;

public interface TransactionService {

    TransactionResponse createTransaction(CreateTransactionRequest request);

    TransactionResponse getTransaction(String transactionId);

    TransactionResponse updateTransactionStatus(
            String transactionId,
            UpdateTransactionStatusRequest request
    );

    List<TransactionResponse> getCustomerTransactions(String customerId);
}