package com.zenalyst.procurement.service;

import com.zenalyst.procurement.domain.ProcurementResult;
import com.zenalyst.procurement.domain.PurchaseRequirement;
import com.zenalyst.procurement.optimization.OptimizationEngine;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class ProcurementService {

    private final OptimizationEngine optimizationEngine;

    public ProcurementService(OptimizationEngine optimizationEngine) {
        this.optimizationEngine = optimizationEngine;
    }

    public ProcurementResult procure(PurchaseRequirement requirement, LocalDate orderDate) {
        return optimizationEngine.optimize(requirement, orderDate);
    }
}
