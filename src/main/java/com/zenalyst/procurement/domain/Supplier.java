package com.zenalyst.procurement.domain;

import java.util.Objects;

public final class Supplier {

    private final String id;
    private final String name;

    public Supplier(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() { return id; }
    public String getName() { return name; }

    @Override
    public boolean equals(Object other) {
        return other instanceof Supplier && Objects.equals(id, ((Supplier) other).id);
    }

    @Override
    public int hashCode() { return Objects.hash(id); }
}
