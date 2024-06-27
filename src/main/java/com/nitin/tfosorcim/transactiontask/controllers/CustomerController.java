package com.nitin.tfosorcim.transactiontask.controllers;

import com.nitin.tfosorcim.transactiontask.Entity.Customer;
import com.nitin.tfosorcim.transactiontask.Service.CustomerService;
import com.nitin.tfosorcim.transactiontask.Service.RewardService;
import com.nitin.tfosorcim.transactiontask.dto.CustomerDTO;
import com.nitin.tfosorcim.transactiontask.response.GenericResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import reactor.core.publisher.Mono;




import java.net.URI;
import java.util.List;


@RestController
@RequestMapping("/api/customer")
@Tag(
        name = "Customer Controller all CRUD API methods",
        description = "This is the class that impelments all CRUD operations related to Customer Schema"
)
public class CustomerController {

    @Autowired
    CustomerService customerService;
    private static final Logger logger = LoggerFactory.getLogger(RewardService.class);


    @PostMapping("/createCustomer")
    @Operation(summary = "Create a new customer",
            description = "Creates a new customer and returns the created customer data",
            responses = {
                    @ApiResponse(responseCode = "201",
                            description = "Customer created successfully",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = CustomerDTO.class))),
                    @ApiResponse(responseCode = "400",
                            description = "Invalid input data",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponse.class)))
            })
    public Mono<ResponseEntity<CustomerDTO>> createCustomer(@RequestBody CustomerDTO customerDTO) {
        Customer customer = new Customer(customerDTO.getName());
        return customerService.createCustomer(customer)
                .map(createdCustomer -> ResponseEntity.created(URI.create("/api/customer/createCustomer" + createdCustomer.getCustomerId()))
                        .body(convertToDTO(createdCustomer)));
    }
    @PutMapping("/{customerId}")
    @Operation(summary = "Update existing customer data",
            description = "Updates the details of an existing customer by their ID",
            responses = {
                    @ApiResponse(responseCode = "200",
                            description = "Customer updated successfully",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = CustomerDTO.class))),
                    @ApiResponse(responseCode = "404",
                            description = "Customer not found",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponse.class))),
                    @ApiResponse(responseCode = "400",
                            description = "Invalid input data",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponse.class)))
            })
    public Mono<ResponseEntity<CustomerDTO>> updateCustomer(@PathVariable Long customerId, @RequestBody CustomerDTO customerDTO) {
        return customerService.updateCustomer(customerId, new Customer(customerDTO.getName()))
                .map(customer -> ResponseEntity.ok(convertToDTO(customer)))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{customerId}")
    @Operation(summary = "Delete customer data",
            description = "Delete customer data by ID",
            responses = {
                    @ApiResponse(responseCode = "200",
                            description = "Data deleted successfully",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = GenericResponse.class))),
                    @ApiResponse(responseCode = "404",
                            description = "Customer not found",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponse.class)))
            })
    public Mono<ResponseEntity<GenericResponse>> deleteCustomer(@PathVariable Long customerId) {
        return customerService.deleteCustomer(customerId)
                .map(id -> ResponseEntity.ok(new GenericResponse("success", "Data deleted successfully")))
                .defaultIfEmpty(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new GenericResponse("error", "Customer not found")));
    }

    @GetMapping("/{customerId}")
    @Operation(summary = "Get customer by ID",
            description = "Retrieves customer data by their ID",
            responses = {
                    @ApiResponse(responseCode = "200",
                            description = "Customer data retrieved successfully",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = CustomerDTO.class))),
                    @ApiResponse(responseCode = "404",
                            description = "Customer not found",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponse.class)))
            })
    public Mono<ResponseEntity<CustomerDTO>> getCustomer(@PathVariable Long customerId) {
        return customerService.getCustomer(customerId)
                .map(customer -> ResponseEntity.ok(convertToDTO(customer)))
                .defaultIfEmpty(ResponseEntity.notFound().build())
                .switchIfEmpty(Mono.defer(() -> {
                    logger.warn("Customer with ID {} not found", customerId);
                    return Mono.just(ResponseEntity.notFound().build());
                }));
    }

    @Operation(summary = "Get all customers",
            description = "Retrieves all customers",
            responses = {
                    @ApiResponse(responseCode = "200",
                            description = "Customers retrieved successfully",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = CustomerDTO.class))),
                    @ApiResponse(responseCode = "404",
                            description = "No customers found",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponse.class)))
            })
    @GetMapping("/customers")
    public Mono<ResponseEntity<Object>> getAllCustomers() {
        return customerService.getAllCustomers()
                .collectList()
                .map(customers -> {
                    if (customers.isEmpty()) {
                        return ResponseEntity.notFound().build();
                    } else {
                        List<CustomerDTO> customerDTOs = customers.stream()
                                .map(this::convertToDTO)
                                .toList();
                        return ResponseEntity.ok(customerDTOs);
                    }
                });
    }

    private CustomerDTO convertToDTO(Customer customer) {
        CustomerDTO dto = new CustomerDTO();
        dto.setId(customer.getCustomerId());
        dto.setName(customer.getName());
        return dto;
    }
}
