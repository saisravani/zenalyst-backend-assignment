package com.zenalyst.procurement.repository;

import com.zenalyst.procurement.domain.Part;
import com.zenalyst.procurement.domain.SupplierPart;

import java.util.List;

public interface SupplierRepository {

    List<SupplierPart> findByPart(Part part);
}
