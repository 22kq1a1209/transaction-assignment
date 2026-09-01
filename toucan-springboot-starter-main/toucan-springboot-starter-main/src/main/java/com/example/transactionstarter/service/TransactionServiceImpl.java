package com.example.transactionstarter.service;

import com.example.transactionstarter.DTO.CreateTransactionRequest;
import com.example.transactionstarter.DTO.TransactionResponse;
import com.example.transactionstarter.DTO.UpdateTransactionStatusRequest;
import com.example.transactionstarter.entity.Transaction;
import com.example.transactionstarter.entity.TransactionStatus;
import com.example.transactionstarter.exception.DuplicateTransactionException;
import com.example.transactionstarter.exception.InvalidTransactionException;
import com.example.transactionstarter.exception.TransactionNotFoundException;
import com.example.transactionstarter.repository.TransactionRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;

    public TransactionServiceImpl(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }


    // 1. Create Transaction
    @Override
    public TransactionResponse createTransaction(
            CreateTransactionRequest request) {

        validateCreateRequest(request);

        // Check duplicate transaction ID
        if (transactionRepository.existsById(request.getTransactionId())) {
            throw new DuplicateTransactionException(
                    "Transaction with ID "
                            + request.getTransactionId()
                            + " already exists"
            );
        }

        // Create entity
        Transaction transaction = new Transaction();

        transaction.setTransactionId(request.getTransactionId());
        transaction.setCustomerId(request.getCustomerId());
        transaction.setAmount(request.getAmount());
        transaction.setCurrency(request.getCurrency());
        transaction.setTransactionType(request.getTransactionType());

        // New transaction always starts with PENDING
        transaction.setStatus(TransactionStatus.PENDING);

        // Save to database
        Transaction savedTransaction =
                transactionRepository.save(transaction);

        return mapToResponse(savedTransaction);
    }


    // 2. Get Transaction
    @Override
    public TransactionResponse getTransaction(String transactionId) {

        if (transactionId == null ||
                transactionId.trim().isEmpty()) {

            throw new InvalidTransactionException(
                    "Transaction ID cannot be empty"
            );
        }

        Transaction transaction = transactionRepository
                .findById(transactionId)
                .orElseThrow(() ->
                        new TransactionNotFoundException(
                                "Transaction with ID "
                                        + transactionId
                                        + " not found"
                        )
                );

        return mapToResponse(transaction);
    }


    // 3. Update Transaction Status
    @Override
    public TransactionResponse updateTransactionStatus(
            String transactionId,
            UpdateTransactionStatusRequest request) {

        if (transactionId == null ||
                transactionId.trim().isEmpty()) {

            throw new InvalidTransactionException(
                    "Transaction ID cannot be empty"
            );
        }

        if (request == null ||
                request.getStatus() == null) {

            throw new InvalidTransactionException(
                    "Status cannot be null"
            );
        }

        Transaction transaction = transactionRepository
                .findById(transactionId)
                .orElseThrow(() ->
                        new TransactionNotFoundException(
                                "Transaction with ID "
                                        + transactionId
                                        + " not found"
                        )
                );

        validateStatusTransition(
                transaction.getStatus(),
                request.getStatus()
        );

        transaction.setStatus(request.getStatus());

        Transaction updatedTransaction =
                transactionRepository.save(transaction);

        return mapToResponse(updatedTransaction);
    }


    // 4. Get Customer Transactions
    @Override
    public List<TransactionResponse> getCustomerTransactions(
            String customerId) {

        if (customerId == null ||
                customerId.trim().isEmpty()) {

            throw new InvalidTransactionException(
                    "Customer ID cannot be empty"
            );
        }

        return transactionRepository
                .findByCustomerId(customerId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }


    // Validation for creating transaction
    private void validateCreateRequest(
            CreateTransactionRequest request) {

        if (request == null) {
            throw new InvalidTransactionException(
                    "Request cannot be null"
            );
        }

        if (request.getTransactionId() == null ||
                request.getTransactionId().trim().isEmpty()) {

            throw new InvalidTransactionException(
                    "Transaction ID cannot be empty"
            );
        }

        if (request.getCustomerId() == null ||
                request.getCustomerId().trim().isEmpty()) {

            throw new InvalidTransactionException(
                    "Customer ID cannot be empty"
            );
        }

        if (request.getAmount() == null ||
                request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {

            throw new InvalidTransactionException(
                    "Amount must be greater than zero"
            );
        }

        if (request.getCurrency() == null ||
                request.getCurrency().trim().isEmpty()) {

            throw new InvalidTransactionException(
                    "Currency cannot be empty"
            );
        }

        if (request.getTransactionType() == null) {

            throw new InvalidTransactionException(
                    "Transaction type cannot be null"
            );
        }
    }


    // Status transition rules
    private void validateStatusTransition(
            TransactionStatus currentStatus,
            TransactionStatus newStatus) {

        if (currentStatus == TransactionStatus.PENDING) {

            if (newStatus == TransactionStatus.COMPLETED ||
                    newStatus == TransactionStatus.FAILED ||
                    newStatus == TransactionStatus.CANCELLED) {

                return;
            }
        }

        throw new InvalidTransactionException(
                "Invalid status transition from "
                        + currentStatus
                        + " to "
                        + newStatus
        );
    }


    // Entity -> DTO
    private TransactionResponse mapToResponse(
            Transaction transaction) {

        return new TransactionResponse(
                transaction.getTransactionId(),
                transaction.getCustomerId(),
                transaction.getAmount(),
                transaction.getCurrency(),
                transaction.getTransactionType(),
                transaction.getStatus()
        );
    }
}