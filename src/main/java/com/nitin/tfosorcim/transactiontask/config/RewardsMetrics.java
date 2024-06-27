package com.nitin.tfosorcim.transactiontask.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class RewardsMetrics {
    @Value("${reward.amount.threshold1}")
    private int rewardAmountThreshold1;//100

    @Value("${reward.amount.threshold2}")
    private int rewardAmountThreshold2;//50

    @Value("${reward.points.rate1}")
    private int pointsRate1;//2

    @Value("${reward.points.rate2}")
    private int pointsRate2;//1

    public int getRewardAmountThreshold1() {
        return rewardAmountThreshold1;
    }

    public int getRewardAmountThreshold2() {
        return rewardAmountThreshold2;
    }

    public int getPointsRate1() {
        return pointsRate1;
    }

    public int getPointsRate2() {
        return pointsRate2;
    }
}
