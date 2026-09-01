package com.example.transactionstarter.DTO;

import com.example.transactionstarter.entity.TransactionStatus;

public class UpdateTransactionStatusRequest {

    private TransactionStatus status;

    public UpdateTransactionStatusRequest() {
    }

    public TransactionStatus getStatus() {
        return status;
    }

    public void setStatus(TransactionStatus status) {
        this.status = status;
    }
}