package com.zenalyst.procurement.optimization;

import com.zenalyst.procurement.domain.PricingTier;
import com.zenalyst.procurement.domain.ProcurementDecision;
import com.zenalyst.procurement.domain.ProcurementResult;
import com.zenalyst.procurement.domain.PurchaseRequirement;
import com.zenalyst.procurement.domain.SupplierPart;
import com.zenalyst.procurement.repository.SupplierRepository;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

@Component
public class OptimizationEngine {

    private final SupplierRepository supplierRepository;

    public OptimizationEngine(SupplierRepository supplierRepository) {
        this.supplierRepository = supplierRepository;
    }

    public ProcurementResult optimize(PurchaseRequirement requirement, LocalDate orderDate) {
        return optimize(requirement, supplierRepository.findByPart(requirement.getPart()), orderDate);
    }

    public ProcurementResult optimize(PurchaseRequirement requirement, List<SupplierPart> candidates,
                                      LocalDate orderDate) {
        Objects.requireNonNull(requirement, "requirement must not be null");
        Objects.requireNonNull(candidates, "candidates must not be null");
        Objects.requireNonNull(orderDate, "orderDate must not be null");

        int requiredQuantity = requirement.getRequiredQuantity();
        if (requiredQuantity < 0) {
            throw new IllegalArgumentException("requiredQuantity must not be negative");
        }

        List<SupplierPart> eligibleSuppliers = candidates.stream()
                .filter(candidate -> !orderDate.plusDays(candidate.getLeadTimeDays())
                        .isAfter(requirement.getRequiredBy()))
                .collect(java.util.stream.Collectors.toList());

        BigDecimal[][] costs = new BigDecimal[eligibleSuppliers.size() + 1][requiredQuantity + 1];
        int[][] quantities = new int[eligibleSuppliers.size() + 1][requiredQuantity + 1];
        costs[0][0] = BigDecimal.ZERO;

        for (int supplierIndex = 1; supplierIndex <= eligibleSuppliers.size(); supplierIndex++) {
            SupplierPart supplierPart = eligibleSuppliers.get(supplierIndex - 1);
            for (int purchasedQuantity = 0; purchasedQuantity <= requiredQuantity; purchasedQuantity++) {
                if (costs[supplierIndex - 1][purchasedQuantity] != null) {
                    costs[supplierIndex][purchasedQuantity] = costs[supplierIndex - 1][purchasedQuantity];
                }
            }

            int maximumOrderQuantity = Math.min(supplierPart.getCapacity(), requiredQuantity);
            for (int orderQuantity = supplierPart.getMinimumOrderQuantity();
                 orderQuantity <= maximumOrderQuantity; orderQuantity++) {
                PricingTier tier = findPricingTier(supplierPart, orderQuantity);
                if (tier == null) {
                    continue;
                }

                BigDecimal orderCost = tier.getUnitPrice().multiply(BigDecimal.valueOf(orderQuantity));
                for (int previousQuantity = 0;
                     previousQuantity + orderQuantity <= requiredQuantity; previousQuantity++) {
                    BigDecimal previousCost = costs[supplierIndex - 1][previousQuantity];
                    if (previousCost == null) {
                        continue;
                    }

                    int newQuantity = previousQuantity + orderQuantity;
                    BigDecimal newCost = previousCost.add(orderCost);
                    if (costs[supplierIndex][newQuantity] == null
                            || newCost.compareTo(costs[supplierIndex][newQuantity]) < 0) {
                        costs[supplierIndex][newQuantity] = newCost;
                        quantities[supplierIndex][newQuantity] = orderQuantity;
                    }
                }
            }
        }

        BigDecimal totalCost = costs[eligibleSuppliers.size()][requiredQuantity];
        if (totalCost == null) {
            return new ProcurementResult(
                    ProcurementResult.Status.UNFULFILLABLE,
                    List.of(),
                    null,
                    List.of("No feasible supplier combination can satisfy the required quantity under the supplier constraints.")
            );
        }

        List<ProcurementDecision> decisions = reconstructDecisions(
                eligibleSuppliers, quantities, requiredQuantity, orderDate);
        return new ProcurementResult(
                ProcurementResult.Status.FULFILLABLE,
                decisions,
                totalCost,
                List.of("The allocation satisfies the required quantity at the minimum total procurement cost.")
        );
    }

    private List<ProcurementDecision> reconstructDecisions(List<SupplierPart> suppliers, int[][] quantities,
                                                            int requiredQuantity, LocalDate orderDate) {
        List<ProcurementDecision> decisions = new ArrayList<>();
        int remainingQuantity = requiredQuantity;

        for (int supplierIndex = suppliers.size(); supplierIndex > 0; supplierIndex--) {
            int orderQuantity = quantities[supplierIndex][remainingQuantity];
            if (orderQuantity == 0) {
                continue;
            }

            SupplierPart supplierPart = suppliers.get(supplierIndex - 1);
            PricingTier tier = findPricingTier(supplierPart, orderQuantity);
            BigDecimal totalCost = tier.getUnitPrice().multiply(BigDecimal.valueOf(orderQuantity));
            decisions.add(new ProcurementDecision(
                    supplierPart.getSupplier(),
                    orderQuantity,
                    tier.getUnitPrice(),
                    totalCost,
                    orderDate.plusDays(supplierPart.getLeadTimeDays()),
                    List.of(
                            "Supplier can meet the required-by date.",
                            "Quantity satisfies the minimum order quantity.",
                            "Quantity is within supplier capacity.",
                            "Applicable volume-pricing tier: " + tier.getMinQuantity() + "-"
                                    + tier.getMaxQuantity() + " at " + tier.getUnitPrice() + " per unit.",
                            "Supplier is part of the minimum-cost allocation."
                    )
            ));
            remainingQuantity -= orderQuantity;
        }

        decisions.sort(Comparator.comparing(decision -> decision.getSupplier().getId()));
        return decisions;
    }

    private PricingTier findPricingTier(SupplierPart supplierPart, int quantity) {
        return supplierPart.getPricingTiers().stream()
                .filter(tier -> tier.getMinQuantity() <= quantity && quantity <= tier.getMaxQuantity())
                .findFirst()
                .orElse(null);
    }
}
