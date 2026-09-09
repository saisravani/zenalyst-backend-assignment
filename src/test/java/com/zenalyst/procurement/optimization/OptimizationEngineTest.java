package com.zenalyst.procurement.optimization;

import com.zenalyst.procurement.domain.Part;
import com.zenalyst.procurement.domain.PricingTier;
import com.zenalyst.procurement.domain.ProcurementDecision;
import com.zenalyst.procurement.domain.ProcurementResult;
import com.zenalyst.procurement.domain.PurchaseRequirement;
import com.zenalyst.procurement.domain.Supplier;
import com.zenalyst.procurement.domain.SupplierPart;
import com.zenalyst.procurement.repository.InMemorySupplierRepository;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class OptimizationEngineTest {

    private final OptimizationEngine engine = new OptimizationEngine(new InMemorySupplierRepository());

    @Test
    void findsMinimumCostAllocationForSampleData() {
        Part brakePad = new Part("part-1", "BRAKE-PAD-001", "Brake Pad");
        ProcurementResult result = engine.optimize(
                new PurchaseRequirement(brakePad, 100, LocalDate.of(2026, 9, 15)),
                LocalDate.of(2026, 9, 10));

        assertThat(result.getStatus()).isEqualTo(ProcurementResult.Status.FULFILLABLE);
        assertThat(result.getTotalCost()).isEqualByComparingTo("975.00");
        assertThat(result.getDecisions()).extracting(ProcurementDecision::getQuantity)
                .containsExactly(50, 50);
        assertThat(result.getDecisions()).extracting(decision -> decision.getSupplier().getName())
                .containsExactly("Alpha Manufacturing", "Beta Components");
    }

    @Test
    void excludesSupplierThatMissesDeadline() {
        Part part = part();
        SupplierPart lateCheapSupplier = supplierPart("late", 11, 1, 100, "1.00", 1, 100);
        SupplierPart onTimeSupplier = supplierPart("on-time", 2, 1, 100, "5.00", 1, 100);

        ProcurementResult result = optimize(part, 10, List.of(lateCheapSupplier, onTimeSupplier));

        assertThat(result.getDecisions()).singleElement()
                .extracting(decision -> decision.getSupplier().getId()).isEqualTo("on-time");
        assertThat(result.getTotalCost()).isEqualByComparingTo("50.00");
    }

    @Test
    void returnsUnfulfillableWhenMoqPreventsEveryExactAllocation() {
        Part part = part();
        SupplierPart first = supplierPart("first", 1, 6, 6, "5.00", 6, 6);
        SupplierPart second = supplierPart("second", 1, 6, 6, "5.00", 6, 6);

        ProcurementResult result = optimize(part, 10, List.of(first, second));

        assertThat(result.getStatus()).isEqualTo(ProcurementResult.Status.UNFULFILLABLE);
        assertThat(result.getDecisions()).isEmpty();
        assertThat(result.getReasons().get(0)).contains("No feasible supplier combination");
    }

    @Test
    void returnsUnfulfillableWhenNoSupplierCombinationCanMeetTheRequirement() {
        Part part = part();
        SupplierPart insufficientCapacity = supplierPart("limited", 1, 1, 40, "5.00", 1, 40);

        ProcurementResult result = optimize(part, 50, List.of(insufficientCapacity));

        assertThat(result.getStatus()).isEqualTo(ProcurementResult.Status.UNFULFILLABLE);
        assertThat(result.getDecisions()).isEmpty();
    }

    @Test
    void usesVolumeDiscountWhenItBeatsSmallQuantitySupplier() {
        Part part = part();
        SupplierPart cheapForSmallOrders = supplierPart("small", 1, 1, 100, "9.00", 1, 100);
        SupplierPart discountedSupplier = new SupplierPart(
                new Supplier("discount", "discount"), part, 1, 1, 100,
                List.of(new PricingTier(1, 49, new BigDecimal("12.00")),
                        new PricingTier(50, 100, new BigDecimal("8.00")))
        );

        ProcurementResult result = optimize(part, 50, List.of(cheapForSmallOrders, discountedSupplier));

        assertThat(result.getDecisions()).singleElement()
                .extracting(decision -> decision.getSupplier().getId()).isEqualTo("discount");
        assertThat(result.getTotalCost()).isEqualByComparingTo("400.00");
    }

    @Test
    void splitsOrderWhenCapacityRequiresItAndCalculatesDeliveryDates() {
        Part part = part();
        SupplierPart first = supplierPart("first", 2, 1, 40, "4.00", 1, 40);
        SupplierPart second = supplierPart("second", 3, 1, 60, "5.00", 1, 60);

        ProcurementResult result = optimize(part, 100, List.of(first, second));

        assertThat(result.getDecisions()).extracting(ProcurementDecision::getQuantity)
                .containsExactly(40, 60);
        assertThat(result.getDecisions()).extracting(ProcurementDecision::getExpectedDeliveryDate)
                .containsExactly(LocalDate.of(2026, 9, 12), LocalDate.of(2026, 9, 13));
    }

    @Test
    void doesNotReuseSupplierMoreThanOnce() {
        Part part = part();
        SupplierPart onlySupplier = supplierPart("only", 1, 3, 3, "1.00", 3, 3);

        ProcurementResult result = optimize(part, 6, List.of(onlySupplier));

        assertThat(result.getStatus()).isEqualTo(ProcurementResult.Status.UNFULFILLABLE);
    }

    private ProcurementResult optimize(Part part, int quantity, List<SupplierPart> candidates) {
        return engine.optimize(
                new PurchaseRequirement(part, quantity, LocalDate.of(2026, 9, 20)),
                candidates,
                LocalDate.of(2026, 9, 10));
    }

    private Part part() {
        return new Part("test-part", "TEST-001", "Test Part");
    }

    private SupplierPart supplierPart(String id, int leadTimeDays, int minimumOrderQuantity, int capacity,
                                      String unitPrice, int tierMinimum, int tierMaximum) {
        Part part = part();
        return new SupplierPart(
                new Supplier(id, id),
                part,
                leadTimeDays,
                minimumOrderQuantity,
                capacity,
                List.of(new PricingTier(tierMinimum, tierMaximum, new BigDecimal(unitPrice)))
        );
    }
}
