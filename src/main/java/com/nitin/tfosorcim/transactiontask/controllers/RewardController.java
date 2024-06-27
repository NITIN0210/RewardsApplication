package com.nitin.tfosorcim.transactiontask.controllers;

import com.nitin.tfosorcim.transactiontask.Service.RewardService;
import com.nitin.tfosorcim.transactiontask.exception.CustomerNotFoundException;
import com.nitin.tfosorcim.transactiontask.exception.InvalidDateRangeException;
import com.nitin.tfosorcim.transactiontask.response.RewardResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.time.YearMonth;
import java.util.*;


@RestController
@RequestMapping("/api/rewards")
@Tag(
        name = "Reward Calculation  API methods",
        description = "This is the class that impelments all Reward Calculation methods"
)

public class RewardController {
    @Autowired
    private RewardService rewardService;
    private static final Logger logger = LoggerFactory.getLogger(RewardController.class);

    @Autowired
    public RewardController(RewardService rewardService) {
        this.rewardService = rewardService;
    }

    @Operation(
            summary = "Monthwise rewards",
            description = "getting month wise rewards of customers based on customer id start date and end date of 3 month period in yyyy-mm format"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Integer.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/for-customer/{customerId}")
    public Mono<ResponseEntity<Map<Long, Map<YearMonth, Integer>>>> getRewardPointsForCustomer(
            @PathVariable Long customerId,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM") YearMonth startMonth,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM") YearMonth endMonth) {
        //logger.info("Received request to get reward points for customer {} from {} to {}", customerId, startMonth, endMonth);

        return rewardService.calculateRewardPoints(customerId, startMonth, endMonth)
                .map(rewardPointsPerMonth -> ResponseEntity.ok(rewardPointsPerMonth))
                .onErrorResume(InvalidDateRangeException.class, e -> {
                    logger.error("Invalid date range: {}", e.getMessage());
                    return Mono.just(ResponseEntity.badRequest().<Map<Long, Map<YearMonth, Integer>>>build());
                })
                .onErrorResume(CustomerNotFoundException.class, e -> {
                    logger.error("Customer not found: {}", e.getMessage());
                    return Mono.just(ResponseEntity.status(HttpStatus.NOT_FOUND).<Map<Long, Map<YearMonth, Integer>>>build());
                })
                .onErrorResume(e -> {
                    logger.error("An unexpected error occurred: {}", e.getMessage());
                    return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).<Map<Long, Map<YearMonth, Integer>>>build());
                });
    }

    @GetMapping("/total-for-customer/{customerId}")
    @Operation(summary = "Get total reward points for a customer within a specified date range",
            description = "Calculates the total reward points earned by a customer between the specified start and end months.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Integer.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)))
    })
    @ResponseBody
    public Mono<ResponseEntity<RewardResponse>> getTotalRewardPointsForCustomer(@PathVariable Long customerId, @RequestParam @DateTimeFormat(pattern = "yyyy-MM") YearMonth startMonth, @RequestParam @DateTimeFormat(pattern = "yyyy-MM") YearMonth endMonth) {
        return rewardService.calculateTotalRewardPoints(customerId, startMonth, endMonth)
                .map(totalRewardPoints -> {
                    String message = "Total reward points for customer " + customerId + " between " + startMonth + " and " + endMonth + " is " + totalRewardPoints;
                    RewardResponse response = new RewardResponse(message);
                    return ResponseEntity.ok(response);
                })
                .onErrorResume(InvalidDateRangeException.class, e -> {
                    String errorMessage = "Invalid date range: " + e.getMessage();
                    RewardResponse response = new RewardResponse(errorMessage);
                    return Mono.just(ResponseEntity.badRequest().body(response));
                })
                .onErrorResume(CustomerNotFoundException.class, e -> {
                    String errorMessage = "Customer not found: " + e.getMessage();
                    RewardResponse response = new RewardResponse(errorMessage);
                    return Mono.just(ResponseEntity.status(HttpStatus.NOT_FOUND).body(response));
                })
                .onErrorResume(e -> {
                    String errorMessage = "An unexpected error occurred: " + e.getMessage();
                    RewardResponse response = new RewardResponse(errorMessage);
                    return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response));
                });    }
}