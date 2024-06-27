package com.nitin.tfosorcim.transactiontask.config;

import com.nitin.tfosorcim.transactiontask.Entity.Transaction;

public class Login {
    private boolean isAuthorizedToDeleteTransaction(Transaction transaction) {
        // Assuming you have some form of authentication and user context available
        // Here, you would implement your authorization logic
        // For example, you might check if the transaction belongs to the authenticated user
        // or if the user has specific roles/permissions to delete transactions

        // Example: Check if the transaction belongs to the authenticated customer
        // For demonstration purposes, let's assume the currentCustomerId is available
        Long currentCustomerId = getCurrentCustomerId();
        return true;// This method retrieves the ID of the currently authenticated customer
        //return transaction.getCustomerId().equals(currentCustomerId);
    }

    private Long getCurrentCustomerId() {
        return 1L;
    }
}

