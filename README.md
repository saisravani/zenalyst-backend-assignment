# Procurement Recommendation Backend

## Overview

This Spring Boot backend recommends how to procure a part requirement at the minimum feasible total cost. Given a part, quantity, order date, and required-by date, it selects supplier allocations that satisfy delivery, MOQ, capacity, and volume-pricing constraints. Each part requirement is optimized independently.

## Tech Stack

- Java 11
- Spring Boot 2.7
- Maven
- Embedded Tomcat
- In-memory supplier repository
- JUnit 5

## How It Works

Only suppliers that can deliver by the required-by date are considered. For each eligible supplier, an order quantity must meet its MOQ, stay within its capacity, and match a pricing tier. Suppliers may be combined to satisfy a requirement.

The optimizer uses dynamic programming to find the minimum-cost allocation that fulfills the exact required quantity. A greedy approach is not sufficient: a larger order can cross a volume-pricing tier and become cheaper overall than selecting the lowest current unit price first.

## Complexity

Let `S` be the number of eligible suppliers and `Q` be the required quantity.

- Time: `O(S × Q²)`
- Space: `O(S × Q)`

The quantity limit on the API request also prevents unbounded input from creating an excessively large optimization state.

## API

### `POST /api/procurement/recommendations`

Sample request for 100 Brake Pads, ordered on 10 September 2026 and required by 15 September 2026:

```json
{
  "partId": "part-1",
  "partCode": "BRAKE-PAD-001",
  "partName": "Brake Pad",
  "requiredQuantity": 100,
  "requiredBy": "2026-09-15",
  "orderDate": "2026-09-10"
}
```

The current sample data produces a minimum-cost allocation of 50 units from Alpha Manufacturing and 50 from Beta Components, with a total cost of ₹975:

```json
{
  "status": "FULFILLABLE",
  "allocations": [
    {
      "supplierId": "supplier-1",
      "supplierName": "Alpha Manufacturing",
      "quantity": 50,
      "unitPrice": 10.00,
      "totalCost": 500.00,
      "expectedDeliveryDate": "2026-09-13",
      "reasons": [
        "Supplier can meet the required-by date.",
        "Quantity satisfies the minimum order quantity.",
        "Quantity is within supplier capacity.",
        "Applicable volume-pricing tier: 50-100 at 10.00 per unit.",
        "Supplier is part of the minimum-cost allocation."
      ]
    },
    {
      "supplierId": "supplier-2",
      "supplierName": "Beta Components",
      "quantity": 50,
      "unitPrice": 9.50,
      "totalCost": 475.00,
      "expectedDeliveryDate": "2026-09-15",
      "reasons": [
        "Supplier can meet the required-by date.",
        "Quantity satisfies the minimum order quantity.",
        "Quantity is within supplier capacity.",
        "Applicable volume-pricing tier: 40-80 at 9.50 per unit.",
        "Supplier is part of the minimum-cost allocation."
      ]
    }
  ],
  "totalCost": 975.00,
  "reasons": [
    "The allocation satisfies the required quantity at the minimum total procurement cost."
  ]
}
```

If no feasible combination can fulfill the exact quantity, the API returns `UNFULFILLABLE` with no partial allocation.

## How to Run

Prerequisites: JDK 11 and Maven 3.6+.

```bash
mvn clean test
mvn spring-boot:run
```

The application starts on `http://localhost:8080` by default.

## Testing

Automated tests cover optimizer behavior (deadlines, MOQ, capacity, volume pricing, and exact fulfillment), service delegation, Spring context startup, and the recommendation API response.

Run the full test suite with:

```bash
mvn test
```

## Assumptions

- Supplier and part data is static and stored in memory.
- Quantities are whole numbers.
- Lead time uses calendar days.
- Delivery on the required-by date is valid.
- Pricing tiers are non-overlapping quantity ranges.
- The unit price from the applicable tier applies to the entire quantity ordered from that supplier.
- Each part requirement is optimized independently.
- A procurement request contains at most one requirement for a given part.
- Supplier capacity is fixed for a planning calculation.
- Full, exact fulfillment is required; the system does not return a partial purchase.
- `requiredQuantity` must be between 1 and 100,000.

## Design Decisions

- **No database:** static sample data keeps the assignment focused on the procurement model and algorithm rather than persistence infrastructure.
- **Dynamic programming over greedy selection:** it evaluates feasible supplier combinations and tier-dependent pricing globally, rather than making locally cheapest choices.
- **Domain objects and API DTOs are separate:** the HTTP contract remains independent from the internal procurement model.
- **Deterministic optimization:** the order date is supplied by the request and explanations are generated from backend rules, so identical inputs produce the same result.

## What Was Left Out and Why

The following are intentionally outside this assignment's scope: database/persistence, inventory synchronization, existing purchase orders, supplier negotiation and history, real-time supplier capacity updates, authentication and authorization, event-driven integration, and AI-generated decisions.

These capabilities are important in a production procurement platform, but the core constraint-based optimization and explainable recommendation behavior can be demonstrated without them.
