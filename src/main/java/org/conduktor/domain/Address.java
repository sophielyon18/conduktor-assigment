package org.conduktor.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Address {
    @JsonProperty("street")
    private String street;

    @JsonProperty("town")
    private String town;

    @JsonProperty("postode")
    private String postcode;
}