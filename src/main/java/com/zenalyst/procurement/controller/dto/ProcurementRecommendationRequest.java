package com.zenalyst.procurement.controller.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Max;
import javax.validation.constraints.Positive;
import java.time.LocalDate;

public class ProcurementRecommendationRequest {

    @NotBlank
    private String partId;

    @NotBlank
    private String partCode;

    @NotBlank
    private String partName;

    @Positive
    @Max(100000)
    private int requiredQuantity;

    @NotNull
    private LocalDate requiredBy;

    @NotNull
    private LocalDate orderDate;

    public String getPartId() { return partId; }
    public void setPartId(String partId) { this.partId = partId; }
    public String getPartCode() { return partCode; }
    public void setPartCode(String partCode) { this.partCode = partCode; }
    public String getPartName() { return partName; }
    public void setPartName(String partName) { this.partName = partName; }
    public int getRequiredQuantity() { return requiredQuantity; }
    public void setRequiredQuantity(int requiredQuantity) { this.requiredQuantity = requiredQuantity; }
    public LocalDate getRequiredBy() { return requiredBy; }
    public void setRequiredBy(LocalDate requiredBy) { this.requiredBy = requiredBy; }
    public LocalDate getOrderDate() { return orderDate; }
    public void setOrderDate(LocalDate orderDate) { this.orderDate = orderDate; }
}
