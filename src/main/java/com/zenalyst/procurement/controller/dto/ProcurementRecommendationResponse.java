package com.zenalyst.procurement.controller.dto;

import java.math.BigDecimal;
import java.util.List;

public final class ProcurementRecommendationResponse {

    private final String status;
    private final List<SupplierAllocationResponse> allocations;
    private final BigDecimal totalCost;
    private final List<String> reasons;

    public ProcurementRecommendationResponse(String status, List<SupplierAllocationResponse> allocations,
                                             BigDecimal totalCost, List<String> reasons) {
        this.status = status;
        this.allocations = List.copyOf(allocations);
        this.totalCost = totalCost;
        this.reasons = List.copyOf(reasons);
    }

    public String getStatus() { return status; }
    public List<SupplierAllocationResponse> getAllocations() { return allocations; }
    public BigDecimal getTotalCost() { return totalCost; }
    public List<String> getReasons() { return reasons; }
}
