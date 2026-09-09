package com.zenalyst.procurement.controller;

import com.zenalyst.procurement.controller.dto.ProcurementRecommendationRequest;
import com.zenalyst.procurement.controller.dto.ProcurementRecommendationResponse;
import com.zenalyst.procurement.controller.dto.SupplierAllocationResponse;
import com.zenalyst.procurement.domain.Part;
import com.zenalyst.procurement.domain.ProcurementDecision;
import com.zenalyst.procurement.domain.ProcurementResult;
import com.zenalyst.procurement.domain.PurchaseRequirement;
import com.zenalyst.procurement.service.ProcurementService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/procurement")
public class ProcurementController {

    private final ProcurementService procurementService;

    public ProcurementController(ProcurementService procurementService) {
        this.procurementService = procurementService;
    }

    @PostMapping("/recommendations")
    public ProcurementRecommendationResponse recommend(
            @Valid @RequestBody ProcurementRecommendationRequest request) {

        if (request.getOrderDate().isAfter(request.getRequiredBy())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "orderDate must not be after requiredBy"
            );
        }

        PurchaseRequirement requirement = new PurchaseRequirement(
                new Part(request.getPartId(), request.getPartCode(), request.getPartName()),
                request.getRequiredQuantity(),
                request.getRequiredBy()
        );
        ProcurementResult result = procurementService.procure(requirement, request.getOrderDate());

        List<SupplierAllocationResponse> allocations = result.getDecisions().stream()
                .map(this::toAllocationResponse)
                .collect(Collectors.toList());
        return new ProcurementRecommendationResponse(
                result.getStatus().name(), allocations, result.getTotalCost(), result.getReasons());
    }

    private SupplierAllocationResponse toAllocationResponse(ProcurementDecision decision) {
        return new SupplierAllocationResponse(
                decision.getSupplier().getId(),
                decision.getSupplier().getName(),
                decision.getQuantity(),
                decision.getUnitPrice(),
                decision.getTotalCost(),
                decision.getExpectedDeliveryDate(),
                decision.getReasons()
        );
    }
}
