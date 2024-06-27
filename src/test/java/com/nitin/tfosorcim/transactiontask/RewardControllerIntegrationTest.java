package com.nitin.tfosorcim.transactiontask;


import com.fasterxml.jackson.databind.ObjectMapper;

import com.nitin.tfosorcim.transactiontask.Service.RewardService;
import com.nitin.tfosorcim.transactiontask.controllers.RewardController;
import com.nitin.tfosorcim.transactiontask.exception.CustomerNotFoundException;
import com.nitin.tfosorcim.transactiontask.exception.InvalidDateRangeException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.junit.runner.RunWith;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.YearMonth;
import java.util.Map;


//@RunWith(SpringRunner.class)
@WebFluxTest(controllers = RewardController.class)
public class RewardControllerIntegrationTest {
    //@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    private WebTestClient webTestClient;
    @MockBean
    private RewardService rewardService;
    @InjectMocks
    private RewardController rewardController;

    private final Long customerId = 3L;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        //webTestClient = WebTestClient.bindToController(rewardController).build();
//        webTestClient = webTestClient.mutate()
//                .responseTimeout(Duration.ofMillis(30000))
//                .build();

    }

    @Test
    public void testGetRewardPointsForCustomer() {

        Long customerId1 = 3L;
        YearMonth startDate = YearMonth.parse("2023-03");
        YearMonth endDate = YearMonth.parse("2023-05");

        Map<Long, Map<YearMonth, Integer>> rewards = Map.of(
                3L, Map.of(YearMonth.of(2023, 3), 90, YearMonth.of(2023, 4), 30));
        Mono<Map<Long, Map<YearMonth, Integer>>> monoRewards = Mono.just(rewards);

        when(rewardService.calculateRewardPoints(customerId1, startDate, endDate)).thenReturn(monoRewards);

        webTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/rewards/for-customer/{customerId}")
                        .queryParam("startMonth", "2023-03")
                        .queryParam("endMonth", "2023-05")
                        .build(customerId))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.['" + customerId + "']['2023-03']").isEqualTo(90)
                .jsonPath("$.['" + customerId + "']['2023-04']").isEqualTo(30);
    }


    @Test
    public void testTotalGetRewardPointsForCustomer() {
        Long customerId1 = 3L;
        YearMonth startDate = YearMonth.parse("2023-03");
        YearMonth endDate = YearMonth.parse("2023-05");

        Integer rewards = 120;
        Mono<Integer> monoRewards = Mono.just(rewards);

        when(rewardService.calculateTotalRewardPoints(customerId1, startDate, endDate)).thenReturn(monoRewards);
    webTestClient.get()
            .uri(uriBuilder -> uriBuilder
                    .path("/api/rewards/total-for-customer/{customerId}")
                    .queryParam("startMonth", "2023-03")
                    .queryParam("endMonth", "2023-05")
                    .build(customerId))
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk()
            .expectBody()
            .jsonPath("$.message").isEqualTo("Total reward points for customer " + customerId + " between 2023-03 and 2023-05 is 120");
    }

    @Test
    public void testGetRewardPointsForCustomer_InvalidDateError() {
        Long customerId = 3L;
        YearMonth startMonth = YearMonth.parse("2023-05");// Assuming this is the invalid part
        YearMonth endMonth = YearMonth.parse("2023-03"); // End date before start date

        when(rewardService.calculateRewardPoints(customerId, startMonth, endMonth))
                .thenReturn(Mono.error(new InvalidDateRangeException("Invalid date range")));

        webTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/rewards/for-customer/{customerId}")
                        .queryParam("startMonth", startMonth)
                        .queryParam("endMonth", endMonth)
                        .build(customerId))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest();
    }
    @Test
    public void testGetRewardPointsForCustomer_CustomerNotFoundError() {
        Long customerId = 999L; // Assuming this customer does not exist
        YearMonth startMonth = YearMonth.parse("2023-01");// Assuming this is the invalid part
        YearMonth endMonth = YearMonth.parse("2023-03");

        when(rewardService.calculateRewardPoints(customerId, startMonth, endMonth))
                .thenReturn(Mono.error(new CustomerNotFoundException("Customer not found")));

        webTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/rewards/for-customer/{customerId}")
                        .queryParam("startMonth", startMonth)
                        .queryParam("endMonth", endMonth)
                        .build(customerId))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound();
    }
    @Test
    public void testGetTotalRewardPointsForCustomer_InvalidDateError() {
        Long customerId = 3L;
        YearMonth startMonth = YearMonth.parse("2023-05");// Assuming this is the invalid part
        YearMonth endMonth = YearMonth.parse("2023-03"); // End date before start date

        when(rewardService.calculateTotalRewardPoints(customerId, startMonth, endMonth))
                .thenReturn(Mono.error(new InvalidDateRangeException("Invalid date range")));

        webTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/rewards/total-for-customer/{customerId}")
                        .queryParam("startMonth", startMonth)
                        .queryParam("endMonth", endMonth)
                        .build(customerId))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest();
    }
    @Test
    public void testGetTotalRewardPointsForCustomer_CustomerNotFoundError() {
        Long customerId = 999L; // Assuming this customer does not exist
        YearMonth startMonth = YearMonth.parse("2023-01");// Assuming this is the invalid part
        YearMonth endMonth = YearMonth.parse("2023-03");

        when(rewardService.calculateTotalRewardPoints(customerId, startMonth, endMonth))
                .thenReturn(Mono.error(new CustomerNotFoundException("Customer not found")));

        webTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/rewards/total-for-customer/{customerId}")
                        .queryParam("startMonth", startMonth)
                        .queryParam("endMonth", endMonth)
                        .build(customerId))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound();
    }

}
