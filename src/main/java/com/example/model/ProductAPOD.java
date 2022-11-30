package com.example.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ProductAPOD {
    public final String sku;
    public final String name;
    public final String description;

    public ProductAPOD(@JsonProperty("sku") String sku,
                       @JsonProperty("name") String name,
                       @JsonProperty("description") String description) {
        this.sku = sku;
        this.name = name;
        this.description = description;
    }
}
