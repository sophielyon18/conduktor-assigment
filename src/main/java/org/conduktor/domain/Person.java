package org.conduktor.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Person {
    @JsonProperty("_id")
    private String id;

    @JsonProperty("name")
    private String name;

    @JsonProperty("dob")
    private String dob;

    @JsonProperty("address")
    private Address address;

    @JsonProperty("telephone")
    private String telephone;

    @JsonProperty("pets")
    private List<String> pets;

    @JsonProperty("score")
    private double score;

    @JsonProperty("email")
    private String email;

    @JsonProperty("url")
    private String url;

    @JsonProperty("description")
    private String description;

    @JsonProperty("verified")
    private boolean verified;

    @JsonProperty("salary")
    private int salary;
}
