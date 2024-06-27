package com.nitin.tfosorcim.transactiontask.Repository;

import com.nitin.tfosorcim.transactiontask.Entity.Customer;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface CustomerRepository extends R2dbcRepository<Customer, Long> {
    Mono<Customer> findByName(String name);



}
