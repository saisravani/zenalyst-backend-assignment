package com.zenalyst.procurement.controller.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public final class SupplierAllocationResponse {

    private final String supplierId;
    private final String supplierName;
    private final int quantity;
    private final BigDecimal unitPrice;
    private final BigDecimal totalCost;
    private final LocalDate expectedDeliveryDate;
    private final List<String> reasons;

    public SupplierAllocationResponse(String supplierId, String supplierName, int quantity, BigDecimal unitPrice,
                                      BigDecimal totalCost, LocalDate expectedDeliveryDate, List<String> reasons) {
        this.supplierId = supplierId;
        this.supplierName = supplierName;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.totalCost = totalCost;
        this.expectedDeliveryDate = expectedDeliveryDate;
        this.reasons = List.copyOf(reasons);
    }

    public String getSupplierId() { return supplierId; }
    public String getSupplierName() { return supplierName; }
    public int getQuantity() { return quantity; }
    public BigDecimal getUnitPrice() { return unitPrice; }
    public BigDecimal getTotalCost() { return totalCost; }
    public LocalDate getExpectedDeliveryDate() { return expectedDeliveryDate; }
    public List<String> getReasons() { return reasons; }
}
