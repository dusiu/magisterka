package pl.edu.uj.dusinski.dao;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class FlightDetailsBothWay {

    private final FlightDetails from;
    private final FlightDetails to;

    @JsonCreator
    public FlightDetailsBothWay(@JsonProperty("from") FlightDetails from,
                                @JsonProperty("to") FlightDetails to) {
        this.from = from;
        this.to = to;
    }

    public FlightDetails getFrom() {
        return from;
    }

    public FlightDetails getTo() {
        return to;
    }
}
