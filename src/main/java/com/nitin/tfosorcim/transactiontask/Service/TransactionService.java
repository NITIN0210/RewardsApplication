package com.nitin.tfosorcim.transactiontask.Service;

import com.nitin.tfosorcim.transactiontask.Entity.Customer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import com.nitin.tfosorcim.transactiontask.Repository.TransactionRepository;
import com.nitin.tfosorcim.transactiontask.Repository.CustomerRepository;
import com.nitin.tfosorcim.transactiontask.Entity.Transaction;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Optional;


@Service
public class TransactionService {
    private final TransactionRepository transactionRepository;
    private final CustomerRepository customerRepository;
    private static final Logger logger = LoggerFactory.getLogger(RewardService.class);


    public TransactionService(TransactionRepository transactionRepository, CustomerRepository customerRepository) {
        this.transactionRepository = transactionRepository;
        this.customerRepository = customerRepository;
    }

    public Mono<Transaction>     createTransaction(Transaction transaction) {
        return transactionRepository.save(transaction);
    }

    public Mono<Transaction> updateTransaction(Long transactionId, Transaction updatedTransaction) {
        return transactionRepository.findById(transactionId)
                .flatMap(existingTransaction -> {
                    existingTransaction.setDate(updatedTransaction.getDate());
                    existingTransaction.setAmount(updatedTransaction.getAmount());
                    return transactionRepository.save(existingTransaction);
                });
    }

    public Mono<Transaction> getTransaction(Long transactionId) {
        return transactionRepository.findById(transactionId);
    }

    public Mono<Customer> findCustomerById(Long customerId) {
        return customerRepository.findById(customerId)
                .switchIfEmpty(Mono.error(new RuntimeException("Customer not found")));    }

    public Flux<Transaction> getAllTransactionsByCustomerId(Long customerId) {
        return transactionRepository.findByCustomerId(customerId);

    }
}
