package com.zenalyst.procurement.repository;

import com.zenalyst.procurement.domain.Part;
import com.zenalyst.procurement.domain.PricingTier;
import com.zenalyst.procurement.domain.Supplier;
import com.zenalyst.procurement.domain.SupplierPart;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Repository
public class InMemorySupplierRepository implements SupplierRepository {

    private final List<SupplierPart> supplierParts;

    public InMemorySupplierRepository() {
        Part brakePad = new Part("part-1", "BRAKE-PAD-001", "Brake Pad");

        Supplier alphaManufacturing = new Supplier("supplier-1", "Alpha Manufacturing");
        Supplier betaComponents = new Supplier("supplier-2", "Beta Components");

        supplierParts = List.of(
                new SupplierPart(
                        alphaManufacturing,
                        brakePad,
                        3,
                        20,
                        100,
                        List.of(
                                new PricingTier(20, 49, new BigDecimal("12.00")),
                                new PricingTier(50, 100, new BigDecimal("10.00"))
                        )
                ),
                new SupplierPart(
                        betaComponents,
                        brakePad,
                        5,
                        10,
                        80,
                        List.of(
                                new PricingTier(10, 39, new BigDecimal("11.50")),
                                new PricingTier(40, 80, new BigDecimal("9.50"))
                        )
                )
        );
    }

    @Override
    public List<SupplierPart> findByPart(Part part) {
        return supplierParts.stream()
                .filter(supplierPart -> supplierPart.getPart().equals(part))
                .collect(Collectors.toList());
    }
}
