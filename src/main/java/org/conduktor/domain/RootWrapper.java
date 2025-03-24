package org.conduktor.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class RootWrapper {

    @JsonProperty("ctRoot")
    private List<Person> ctRoot;
}
