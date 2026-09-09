package com.zenalyst.procurement.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ProcurementControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void returnsRecommendationForValidRequest() throws Exception {
        mockMvc.perform(post("/api/procurement/recommendations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"partId\":\"part-1\",\"partCode\":\"BRAKE-PAD-001\","
                                + "\"partName\":\"Brake Pad\",\"requiredQuantity\":100,"
                                + "\"requiredBy\":\"2026-09-15\",\"orderDate\":\"2026-09-10\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("FULFILLABLE"))
                .andExpect(jsonPath("$.totalCost").value(975))
                .andExpect(jsonPath("$.allocations.length()").value(2))
                .andExpect(jsonPath("$.allocations[0].quantity").value(50))
                .andExpect(jsonPath("$.allocations[0].expectedDeliveryDate").value("2026-09-13"))
                .andExpect(jsonPath("$.allocations[0].reasons").isNotEmpty());
    }

    @Test
    void rejectsQuantityAboveMaximum() throws Exception {
        mockMvc.perform(post("/api/procurement/recommendations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"partId\":\"part-1\",\"partCode\":\"BRAKE-PAD-001\","
                                + "\"partName\":\"Brake Pad\",\"requiredQuantity\":100001,"
                                + "\"requiredBy\":\"2026-09-15\",\"orderDate\":\"2026-09-10\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void rejectsOrderDateAfterRequiredBy() throws Exception {
        mockMvc.perform(post("/api/procurement/recommendations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"partId\":\"part-1\",\"partCode\":\"BRAKE-PAD-001\","
                                + "\"partName\":\"Brake Pad\",\"requiredQuantity\":100,"
                                + "\"requiredBy\":\"2026-09-10\",\"orderDate\":\"2026-09-15\"}"))
                .andExpect(status().isBadRequest());
    }
}
