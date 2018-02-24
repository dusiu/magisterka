package pl.edu.uj.dusinski.dao;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public enum Airline {

    @JsonProperty("wizzair")
    WIZZAIR("wizzair"),
    @JsonProperty("unknown")
    UNKNOWN("unknown"),
    WIZZIAR("asd");

    private final String value;

    @JsonCreator
    Airline(String value) {
        this.value = value;
    }

    private static Map<String, Airline> mapping = initMap();

    private static Map<String, Airline> initMap() {
        HashMap<String, Airline> stringEnumMap = new HashMap<>();
        Arrays.stream(values()).forEach(v -> stringEnumMap.put(v.getValue(), v));
        return stringEnumMap;
    }

    public static Airline fromString(String airline) {
        return mapping.getOrDefault(airline, UNKNOWN);
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return value;
    }
}
