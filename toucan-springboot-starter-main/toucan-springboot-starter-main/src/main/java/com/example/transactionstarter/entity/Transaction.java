package com.example.transactionstarter.entity;


import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "Transaction")
public class Transaction {

    @Id
    @Column(name = "transaction_id", nullable = false, unique = true)
    private String transactionId;

    @Column(name = "customer_id", nullable = false)
    private String customerId;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;

    @Column(nullable = false)
    private String currency;

    @Enumerated(EnumType.STRING)
    @Column(name = "transaction_type", nullable = false)
    private TransactionType transactionType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionStatus status;


    // No- argument constructor
    public Transaction() {
    }


    // Parameterized constructor
    public Transaction(String transactionId,
                       String customerId,
                       BigDecimal amount,
                       String currency,
                       TransactionType transactionType,
                       TransactionStatus status) {

        this.transactionId = transactionId;
        this.customerId = customerId;
        this.amount = amount;
        this.currency = currency;
        this.transactionType = transactionType;
        this.status = status;
    }


    // Getters and Setters

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }


    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }


    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }


    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }


    public TransactionType getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(TransactionType transactionType) {
        this.transactionType = transactionType;
    }


    public TransactionStatus getStatus() {
        return status;
    }

    public void setStatus(TransactionStatus status) {
        this.status = status;
    }
}
