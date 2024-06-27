package com.nitin.tfosorcim.transactiontask.Repository;

import com.nitin.tfosorcim.transactiontask.Entity.Transaction;


import java.time.LocalDate;

import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Flux;


public interface TransactionRepository extends R2dbcRepository<Transaction, Long> {
    Flux<Transaction> findAllByCustomerIdAndDateBetween(Long customer_id, LocalDate startDate, LocalDate endDate);
    Flux<Transaction> findByCustomerId(Long customer_id);


}