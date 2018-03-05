package pl.edu.uj.dusinski.dao;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public enum Airline {

    @JsonProperty("WIZZAIR")
    WIZZAIR("WIZZAIR"),
    @JsonProperty("RYANAIR")
    RYANAIR("RYANAIR"),
    @JsonProperty("UNKNOWN")
    UNKNOWN("UNKNOWN");

    private final String value;

    @JsonCreator
    Airline(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return value;
    }
}
