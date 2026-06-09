package com.kehai.api.scoring;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "kehai.scoring")
public class ScoringProperties {

    private int batchSize = 500;
    private int timeHorizonDays = 30;
    private RiskThresholds riskThresholds = new RiskThresholds();

    public static class RiskThresholds {
        private double high = 0.70;
        private double medium = 0.40;

        public double getHigh() { return high; }
        public void setHigh(double high) { this.high = high; }
        public double getMedium() { return medium; }
        public void setMedium(double medium) { this.medium = medium; }
    }

    public int getBatchSize() { return batchSize; }
    public void setBatchSize(int batchSize) { this.batchSize = batchSize; }
    public int getTimeHorizonDays() { return timeHorizonDays; }
    public void setTimeHorizonDays(int days) { this.timeHorizonDays = days; }
    public RiskThresholds getRiskThresholds() { return riskThresholds; }
    public void setRiskThresholds(RiskThresholds r) { this.riskThresholds = r; }
}
