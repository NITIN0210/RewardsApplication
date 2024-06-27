package com.nitin.tfosorcim.transactiontask;

import com.nitin.tfosorcim.transactiontask.Service.RewardService;
import com.nitin.tfosorcim.transactiontask.config.RewardsMetrics;
import com.nitin.tfosorcim.transactiontask.exception.CustomerNotFoundException;
import com.nitin.tfosorcim.transactiontask.exception.InvalidDateRangeException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.YearMonth;
import java.util.Map;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
public class RewardServiceTest {

    @Autowired
    RewardsMetrics rewardsMetrics;
    @Autowired
    RewardService rewardService;

    @Test
    public void testCalculatePoints_NoPoints() {
        assertEquals(0, rewardService.calculatePoints(50));
    }

    @Test
    public void testCalculatePoints_PointsForAmountBetween50And100() {
        assertEquals(50, rewardService.calculatePoints(100));
    }

    @Test
    public void testCalculatePoints_PointsForAmountOver100() {
        assertEquals(90, rewardService.calculatePoints(120));
    }

    @Test
    public void testCalculatePoints_CornerCase_Exactly50() {
        assertEquals(0, rewardService.calculatePoints(50));
    }

    @Test
    public void testCalculatePoints_CornerCase_Exactly100() {
        assertEquals(50, rewardService.calculatePoints(100));
    }

    @Test
    public void testCalculatePoints_CornerCase_JustOver100() {
        assertEquals(52, rewardService.calculatePoints(101));
    }

    @Test
    public void testCalculateRewardPoints(){
        Map<Long, Map<YearMonth, Integer>> rewards = Map.of(
                3L, Map.of(YearMonth.of(2023, 3), 90, YearMonth.of(2023, 4), 30));

        Mono<Map<Long, Map<YearMonth, Integer>>> resultMono = rewardService.calculateRewardPoints(3L, YearMonth.of(2023, 03), YearMonth.of(2023, 05));
        assertEquals(rewards, resultMono.block());

    }
    @Test
    public void testCalculateRewardPoints_CustomerNotFound() {
        Long customerId = 999L; // Assuming this customer ID does not exist
        YearMonth start = YearMonth.of(2023, 3);
        YearMonth end = YearMonth.of(2023, 5);

        CustomerNotFoundException thrown = assertThrows(CustomerNotFoundException.class, () -> {
            rewardService.calculateRewardPoints(customerId, start, end).block();
        });

        assertEquals("Customer not found with ID: "+customerId, thrown.getMessage());
    }
    @Test
    public void testCalculateRewardPoints_InvalidDateRange() {
        Long customerId = 3L;
        YearMonth start = YearMonth.of(2023, 5);
        YearMonth end = YearMonth.of(2023, 3);

        InvalidDateRangeException thrown = assertThrows(InvalidDateRangeException.class, () -> {
            rewardService.calculateRewardPoints(customerId, start, end).block();
        });

        assertEquals("Invalid date range", thrown.getMessage());
    }
    @Test
    public void testCalculateTotalRewardPoints(){
        Integer rewards = 120;
        Mono<Integer> monoRewards = Mono.just(rewards);

        Mono<Integer> resultMono = rewardService.calculateTotalRewardPoints(3L, YearMonth.of(2023, 03), YearMonth.of(2023, 05));
        assertEquals(rewards, resultMono.block());

    }
    @Test
    public void testCalculateTotalRewardPoints_CustomerNotFound() {
        Long customerId = 999L; // Assuming this customer ID does not exist
        YearMonth start = YearMonth.of(2023, 3);
        YearMonth end = YearMonth.of(2023, 5);

        CustomerNotFoundException thrown = assertThrows(CustomerNotFoundException.class, () -> {
            rewardService.calculateTotalRewardPoints(customerId, start, end).block();
        });

        assertEquals("Customer not found with ID: "+customerId, thrown.getMessage());
    }
    @Test
    public void testCalculateTotalRewardPoints_InvalidDateRange() {
        Long customerId = 3L;
        YearMonth start = YearMonth.of(2023, 5);
        YearMonth end = YearMonth.of(2023, 3);

        InvalidDateRangeException thrown = assertThrows(InvalidDateRangeException.class, () -> {
            rewardService.calculateTotalRewardPoints(customerId, start, end).block();
        });

        assertEquals("Invalid date range", thrown.getMessage());
    }
}
