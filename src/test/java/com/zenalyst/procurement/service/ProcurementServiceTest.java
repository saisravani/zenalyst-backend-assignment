package com.zenalyst.procurement.service;

import com.zenalyst.procurement.domain.Part;
import com.zenalyst.procurement.domain.ProcurementResult;
import com.zenalyst.procurement.domain.PurchaseRequirement;
import com.zenalyst.procurement.optimization.OptimizationEngine;
import com.zenalyst.procurement.repository.InMemorySupplierRepository;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class ProcurementServiceTest {

    @Test
    void delegatesProcurementToOptimizationEngine() {
        ProcurementService service = new ProcurementService(
                new OptimizationEngine(new InMemorySupplierRepository()));
        PurchaseRequirement requirement = new PurchaseRequirement(
                new Part("part-1", "BRAKE-PAD-001", "Brake Pad"),
                100,
                LocalDate.of(2026, 9, 15));

        ProcurementResult result = service.procure(requirement, LocalDate.of(2026, 9, 10));

        assertThat(result.getStatus()).isEqualTo(ProcurementResult.Status.FULFILLABLE);
        assertThat(result.getTotalCost()).isEqualByComparingTo("975.00");
    }
}
