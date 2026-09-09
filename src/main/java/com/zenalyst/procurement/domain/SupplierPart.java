package com.zenalyst.procurement.domain;

import java.util.List;

public final class SupplierPart {

    private final Supplier supplier;
    private final Part part;
    private final int leadTimeDays;
    private final int minimumOrderQuantity;
    private final int capacity;
    private final List<PricingTier> pricingTiers;

    public SupplierPart(Supplier supplier, Part part, int leadTimeDays, int minimumOrderQuantity,
                        int capacity, List<PricingTier> pricingTiers) {
        this.supplier = supplier;
        this.part = part;
        this.leadTimeDays = leadTimeDays;
        this.minimumOrderQuantity = minimumOrderQuantity;
        this.capacity = capacity;
        this.pricingTiers = List.copyOf(pricingTiers);
    }

    public Supplier getSupplier() { return supplier; }
    public Part getPart() { return part; }
    public int getLeadTimeDays() { return leadTimeDays; }
    public int getMinimumOrderQuantity() { return minimumOrderQuantity; }
    public int getCapacity() { return capacity; }
    public List<PricingTier> getPricingTiers() { return pricingTiers; }
}
