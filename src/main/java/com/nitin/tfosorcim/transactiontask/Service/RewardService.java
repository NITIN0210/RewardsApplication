package com.nitin.tfosorcim.transactiontask.Service;

import com.nitin.tfosorcim.transactiontask.Entity.Customer;
import com.nitin.tfosorcim.transactiontask.Entity.Transaction;
import com.nitin.tfosorcim.transactiontask.Repository.CustomerRepository;
import com.nitin.tfosorcim.transactiontask.config.RewardsMetrics;
import com.nitin.tfosorcim.transactiontask.Repository.TransactionRepository;
import com.nitin.tfosorcim.transactiontask.exception.CustomerNotFoundException;
import com.nitin.tfosorcim.transactiontask.exception.InvalidDateRangeException;
import lombok.NoArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@NoArgsConstructor
@Service
public class RewardService {
    @Autowired
    private TransactionRepository transactionRepository;
    private CustomerRepository customerRepository;

    private static final Logger logger = LoggerFactory.getLogger(RewardService.class);

    @Autowired
    private RewardsMetrics rewardsMetrics;

    @Autowired
    public RewardService(RewardsMetrics rewardsMetrics, CustomerRepository customerRepository) {
        this.rewardsMetrics = rewardsMetrics;
    }
    // Service method to find a customer
    public Mono<Customer> findCustomerById(Long customerId) {
        return customerRepository.findById(customerId)
                .switchIfEmpty(Mono.error(new CustomerNotFoundException("Customer not found")));
    }
    public Mono<Map<Long, Map<YearMonth, Integer>>> calculateRewardPoints(Long customerId, YearMonth startMonth, YearMonth endMonth) {
        //logger.info("Calculating reward points for customer {} from {} to {}", customerId, startMonth, endMonth);
//        if (startMonth.isAfter(endMonth)) {
//            logger.error("Error occurred while calculating reward points for customer {}: Incorrect input Start month is later than end month", customerId);
//            throw new InvalidDateRangeException("Start month must be before end month");
//        }
//
//        if (findCustomerById(customerId)==null){
//            throw new CustomerNotFoundException("Customer not found with ID: "+ customerId);
//        }

        return Mono.just(new Object()) // Creating a Mono to start the reactive chain
                .flatMap(ignored -> {
                    if (startMonth.isAfter(endMonth)) {
                        return Mono.error(new InvalidDateRangeException("Invalid date range"));
                    }

                    return Mono.just(new Object()); // Continue the flow
                })
                .thenMany(Flux.generate(() -> startMonth, (state, sink) -> {
                            sink.next(state);
                            YearMonth next = state.plusMonths(1);
                            if (!next.isAfter(endMonth)) {
                                return next;
                            } else {
                                sink.complete();
                                return state;
                            }
                        })
                        .cast(YearMonth.class))
                .flatMap(month -> transactionRepository.findAllByCustomerIdAndDateBetween(customerId, month.atDay(1), month.atEndOfMonth())
                        .collectList()
                        .map(transactions -> {
                            Map<Long, Map<YearMonth, Integer>> rewardPointsPerCustomerPerMonth = new HashMap<>();
                            for (Transaction transaction : transactions) {
                                Long transCustomerId = transaction.getCustomerId();
                                if (transCustomerId == null) {
                                    throw new CustomerNotFoundException("Customer not found with ID: " + customerId);
                                }
                                int rewardPoints = calculatePoints(transaction.getAmount());
                                rewardPointsPerCustomerPerMonth.putIfAbsent(transCustomerId, new HashMap<>());
                                rewardPointsPerCustomerPerMonth.get(transCustomerId).merge(month, rewardPoints, Integer::sum);
                            }
                            return rewardPointsPerCustomerPerMonth;
                        }))
                .reduce(new HashMap<>(), (Map<Long, Map<YearMonth, Integer>> map1, Map<Long, Map<YearMonth, Integer>> map2) -> {
                    map2.forEach((Long key, Map<YearMonth, Integer> value) -> map1.merge(key, value, (Map<YearMonth, Integer> v1, Map<YearMonth, Integer> v2) -> {
                        v2.forEach((YearMonth k, Integer v) -> v1.merge(k, v, Integer::sum));
                        return v1;
                    }));
                    return map1;
                })
                .doOnSuccess(map -> {
                    if (map.isEmpty()) {
                        throw new CustomerNotFoundException("Customer not found with ID: " + customerId);
                    }
                   // logger.info("Total reward points for customer {}: {}", customerId, map);
                })
                .onErrorResume(e -> {
                    logger.error("An error occurred while calculating reward points for customer {}: {}", customerId, e.getMessage());
                    return Mono.error(e);
                });
    }
    public Mono<Integer> calculateTotalRewardPoints(Long customerId, YearMonth startMonth, YearMonth endMonth) {
        return calculateRewardPoints(customerId, startMonth, endMonth)
                .flatMap(rewardPointsMap -> {
                    Map<YearMonth, Integer> customerPointsMap = rewardPointsMap.get(customerId);
                    if (customerPointsMap == null) {
                        return Mono.error(new CustomerNotFoundException("Customer not found with ID: " + customerId));
                    }
                    int totalPoints = customerPointsMap.values().stream().mapToInt(value -> value).sum();
                    return Mono.just(totalPoints);
                })
                .onErrorResume(e -> {
                    logger.error("An error occurred while calculating total reward points for customer {}: {}", customerId, e.getMessage());
                    return Mono.error(e);
                });
    }




//    public Mono<Integer> calculateTotalRewardPoints(Long customerId, YearMonth startMonth, YearMonth endMonth) {
//        //logger.info("Calculating reward points for customer {} from {} to {}", customerId, startMonth, endMonth);
//
////        if (startMonth.isAfter(endMonth)) {
////            logger.error("Error occurred while calculating reward points for customer {}: Incorrect input Start month is later than end month", customerId);
////            return Mono.error(new InvalidDateRangeException("Start month must be before end month"));
////        }
////        if (findCustomerById(customerId)==null){
////            throw new CustomerNotFoundException("Customer not found with ID: "+ customerId);
////        }
//
//        return Mono.just(new Object()) // Creating a Mono to start the reactive chain
//                .flatMap(ignored -> {
//                    if (startMonth.isAfter(endMonth)) {
//                        return Mono.error(new InvalidDateRangeException("Invalid date range"));
//                    }
//
//                    return Mono.just(new Object()); // Continue the flow
//                })
//                // Check if the customer exists
//                .then(findCustomerById(customerId)
//                        .switchIfEmpty(Mono.error(new CustomerNotFoundException("Customer not found with ID: " + customerId))))
//
//                .then(Mono.just(startMonth)
//                .expand(month -> month.plusMonths(1).isAfter(endMonth) ? Mono.empty() : Mono.just(month.plusMonths(1)))
//                .flatMap(month -> {
//                    logger.info("Calculating reward points for customer {} in {}", customerId, month);
//
//                    return calculateRewardPointsForCustomer(customerId, month);
//                })
//                .reduce(0, Integer::sum))
//                .doOnSuccess(totalRewardPoints -> {
//                    logger.info("Total reward points for customer {}: {}", customerId, totalRewardPoints);
//                    if (totalRewardPoints == 0) {
//                        logger.warn("No transactions found for customer {} within the specified date range", customerId);
//                    }
//                });
//    }

    public int calculatePoints(double amount) {
        int rewardPoints = 0;

        // Calculate reward points based on the first threshold
        if (amount > rewardsMetrics.getRewardAmountThreshold1()) {
            rewardPoints += (int) ((amount - rewardsMetrics.getRewardAmountThreshold1()) * rewardsMetrics.getPointsRate1()); // 2 points for every dollar over $100
        }
        // Calculate reward points based on the second threshold
        if (amount > rewardsMetrics.getRewardAmountThreshold2()) {
            // Calculate the remaining amount up to the first threshold
            int remAmount = (int) Math.min(amount, rewardsMetrics.getRewardAmountThreshold1());//Math.min(amount,100)
            rewardPoints += (remAmount - rewardsMetrics.getRewardAmountThreshold2()) * rewardsMetrics.getPointsRate2(); // 1 point for every dollar over $50(
        }
       // logger.debug("Calculated {} points for transaction of amount {}", rewardPoints, amount);

        return rewardPoints;
    }
}
