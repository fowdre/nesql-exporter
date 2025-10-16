package com.github.dcysteine.nesql.sql.gregtech;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.ManyToOne;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Embeddable
@EqualsAndHashCode
@Getter
@ToString
public class GregTechRecipeMetadata {
    @Column(nullable = false)
    private String key;
    private long value;

    public GregTechRecipeMetadata() {}
    public GregTechRecipeMetadata(String key, long value)
    {
        this.key = key;
        this.value = value;
    }
}
