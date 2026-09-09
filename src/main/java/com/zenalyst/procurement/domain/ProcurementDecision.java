package com.zenalyst.procurement.domain;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public final class ProcurementDecision {

    private final Supplier supplier;
    private final int quantity;
    private final BigDecimal unitPrice;
    private final BigDecimal totalCost;
    private final LocalDate expectedDeliveryDate;
    private final List<String> reasons;

    public ProcurementDecision(Supplier supplier, int quantity, BigDecimal unitPrice, BigDecimal totalCost,
                               LocalDate expectedDeliveryDate, List<String> reasons) {
        this.supplier = supplier;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.totalCost = totalCost;
        this.expectedDeliveryDate = expectedDeliveryDate;
        this.reasons = List.copyOf(reasons);
    }

    public Supplier getSupplier() { return supplier; }
    public int getQuantity() { return quantity; }
    public BigDecimal getUnitPrice() { return unitPrice; }
    public BigDecimal getTotalCost() { return totalCost; }
    public LocalDate getExpectedDeliveryDate() { return expectedDeliveryDate; }
    public List<String> getReasons() { return reasons; }
}
