package com.zenalyst.procurement.domain;

import java.math.BigDecimal;

public final class PricingTier {

    private final int minQuantity;
    private final int maxQuantity;
    private final BigDecimal unitPrice;

    public PricingTier(int minQuantity, int maxQuantity, BigDecimal unitPrice) {
        this.minQuantity = minQuantity;
        this.maxQuantity = maxQuantity;
        this.unitPrice = unitPrice;
    }

    public int getMinQuantity() { return minQuantity; }
    public int getMaxQuantity() { return maxQuantity; }
    public BigDecimal getUnitPrice() { return unitPrice; }
}
