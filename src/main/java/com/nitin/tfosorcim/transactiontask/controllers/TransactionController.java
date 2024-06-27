package com.nitin.tfosorcim.transactiontask.controllers;

import com.nitin.tfosorcim.transactiontask.Entity.Customer;
import com.nitin.tfosorcim.transactiontask.Entity.Transaction;
import com.nitin.tfosorcim.transactiontask.Service.TransactionService;
import com.nitin.tfosorcim.transactiontask.dto.CustomerDTO;
import com.nitin.tfosorcim.transactiontask.dto.TransactionDTO;
import com.nitin.tfosorcim.transactiontask.response.ResponseHandler;
import com.nitin.tfosorcim.transactiontask.response.GenericResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/transaction")
@Tag(
        name = "Customer Transaction all CRUD API methods",
        description = "This is the class that impelments all CRUD operations related to Transaction Schema"
)

public class TransactionController {

    @Autowired
    private TransactionService transactionService;

    @PostMapping("/createTransaction")
    @Operation(summary = "Create a new Transaction",
            description = "Creates a new transaction and returns the created transaction data",
            responses = {
                    @ApiResponse(responseCode = "201",
                            description = "Transaction created successfully",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = CustomerDTO.class))),
                    @ApiResponse(responseCode = "400",
                            description = "Invalid input data",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponse.class)))
            })
    public Mono<ResponseEntity<TransactionDTO>> createTransaction(@RequestBody TransactionDTO transactionDTO) {
        Transaction transaction = new Transaction(transactionDTO.getDate(), transactionDTO.getAmount(), transactionDTO.getCustomerId()); // Use customerId directly

        return transactionService.createTransaction(transaction)
                .map(createdTransaction -> ResponseEntity
                        .created(URI.create("/api/transaction/createTransaction/" + createdTransaction.getTransactionId()))
                        .body(convertToDTO(createdTransaction)));
    }


    @PutMapping("/{transactionId}")
    @Operation(summary = "Update existing Transaction data",
            description = "Updates the details of an existing Transaction by their ID",
            responses = {
                    @ApiResponse(responseCode = "200",
                            description = "Transaction updated successfully",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = CustomerDTO.class))),
                    @ApiResponse(responseCode = "404",
                            description = "Transaction not found",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponse.class))),
                    @ApiResponse(responseCode = "400",
                            description = "Invalid input data",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponse.class)))
            })
    public Mono<ResponseEntity<TransactionDTO>> updateTransaction(@PathVariable Long transactionId, @RequestBody TransactionDTO transactionDTO) {
        return transactionService.updateTransaction(transactionId, new Transaction(transactionDTO.getDate(), transactionDTO.getAmount(), null))
                .map(transaction -> ResponseEntity.ok(convertToDTO(transaction)))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @GetMapping("/{transactionId}")
    @Operation(summary = "Get Transaction by ID",
            description = "Retrieves Transaction data by their ID",
            responses = {
                    @ApiResponse(responseCode = "200",
                            description = "Transaction data retrieved successfully",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = CustomerDTO.class))),
                    @ApiResponse(responseCode = "404",
                            description = "Transaction not found",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponse.class)))
            })
    public Mono<ResponseEntity<TransactionDTO>> getTransaction(@PathVariable Long transactionId) {
        return transactionService.getTransaction(transactionId)
                .map(transaction -> ResponseEntity.ok(convertToDTO(transaction)))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @GetMapping("/all/{customerId}")
    public Mono<ResponseEntity<Object>> getAllTransactions(@PathVariable Long customerId) {
        return transactionService.getAllTransactionsByCustomerId(customerId)
                .collectList()
                .map(transactions -> {
                    if (transactions.isEmpty()) {
                        return ResponseEntity.notFound().build();
                    } else {
                        List<TransactionDTO> transactionDTOs = transactions.stream()
                                .map(this::convertToDTO)
                                .collect(Collectors.toList());
                        return ResponseEntity.ok(transactionDTOs);
                    }
                });
    }

    private TransactionDTO convertToDTO(Transaction transaction) {
        TransactionDTO dto = new TransactionDTO();
        dto.setId(transaction.getTransactionId());
        dto.setDate(transaction.getDate());
        dto.setAmount(transaction.getAmount());
        dto.setCustomerId(transaction.getCustomerId());
        return dto;
    }
}

