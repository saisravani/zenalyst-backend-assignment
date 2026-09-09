package com.zenalyst.procurement.domain;

import java.math.BigDecimal;
import java.util.List;

public final class ProcurementResult {

    public enum Status {
        FULFILLABLE,
        UNFULFILLABLE
    }

    private final Status status;
    private final List<ProcurementDecision> decisions;
    private final BigDecimal totalCost;
    private final List<String> reasons;

    public ProcurementResult(Status status, List<ProcurementDecision> decisions, BigDecimal totalCost,
                             List<String> reasons) {
        this.status = status;
        this.decisions = List.copyOf(decisions);
        this.totalCost = totalCost;
        this.reasons = List.copyOf(reasons);
    }

    public Status getStatus() { return status; }
    public List<ProcurementDecision> getDecisions() { return decisions; }
    public BigDecimal getTotalCost() { return totalCost; }
    public List<String> getReasons() { return reasons; }
}
