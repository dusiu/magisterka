package pl.edu.uj.dusinski.dao;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class FlightDetailsBothWay {

    private final FlightDetails from;
    private final List<FlightDetails> to;

    @JsonCreator
    public FlightDetailsBothWay(@JsonProperty("from") FlightDetails from,
                                @JsonProperty("to") List<FlightDetails> to) {
        this.from = from;
        this.to = to;
    }

    public FlightDetails getFrom() {
        return from;
    }

    public List<FlightDetails> getTo() {
        return to;
    }
}
