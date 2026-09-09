package com.zenalyst.procurement.domain;

import java.util.Objects;

public final class Part {

    private final String id;
    private final String code;
    private final String name;

    public Part(String id, String code, String name) {
        this.id = id;
        this.code = code;
        this.name = name;
    }

    public String getId() { return id; }
    public String getCode() { return code; }
    public String getName() { return name; }

    @Override
    public boolean equals(Object other) {
        return other instanceof Part && Objects.equals(id, ((Part) other).id);
    }

    @Override
    public int hashCode() { return Objects.hash(id); }
}
