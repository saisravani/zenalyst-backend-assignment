package com.zenalyst.procurement.domain;

import java.time.LocalDate;

public final class PurchaseRequirement {

    private final Part part;
    private final int requiredQuantity;
    private final LocalDate requiredBy;

    public PurchaseRequirement(Part part, int requiredQuantity, LocalDate requiredBy) {
        this.part = part;
        this.requiredQuantity = requiredQuantity;
        this.requiredBy = requiredBy;
    }

    public Part getPart() { return part; }
    public int getRequiredQuantity() { return requiredQuantity; }
    public LocalDate getRequiredBy() { return requiredBy; }
}
