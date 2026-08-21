package com.aryaa.sdm.model;

/**
 * Patient triage severity. Ordinal order (LOW &lt; MEDIUM &lt; HIGH) is used
 * directly for priority-queue comparisons, and each level carries its own
 * base treatment cost for billing — both were magic numbers/ints in the
 * original console app.
 */
public enum Severity {
    LOW(500.0),
    MEDIUM(1000.0),
    HIGH(2000.0);

    private final double baseCost;

    Severity(double baseCost) {
        this.baseCost = baseCost;
    }

    public double getBaseCost() {
        return baseCost;
    }
}
