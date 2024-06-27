package com.nitin.tfosorcim.transactiontask.Service;

import com.nitin.tfosorcim.transactiontask.Entity.Customer;
import com.nitin.tfosorcim.transactiontask.Repository.CustomerRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


@Service
public class CustomerService {

    private final CustomerRepository customerRepository;

    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public Mono<Customer> createCustomer(Customer customer){
        return customerRepository.save(customer);
    }

    public Mono<Customer> updateCustomer(Long customerId, Customer customer) {
        return customerRepository.findById(customerId)
                .flatMap(existingCustomer -> {
                    existingCustomer.setName(customer.getName());
                    return customerRepository.save(existingCustomer);
                });
    }

    public Mono<Long> deleteCustomer(Long customerId) {
        return customerRepository.findById(customerId)
                .flatMap(customer -> customerRepository.delete(customer).then(Mono.just(customer.getCustomerId())));
    }

    public Mono<Customer> getCustomer(Long customerId) {
        return customerRepository.findById(customerId);
    }
    public Flux<Customer> getAllCustomers() {
        return customerRepository.findAll();
    }

}
