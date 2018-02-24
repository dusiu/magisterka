package pl.edu.uj.dusinski.dao;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.springframework.data.annotation.Id;

import java.time.LocalDateTime;

public class DirectionRefreshDetails {

    @Id
    private final long id;
    private final LocalDateTime updatingTime;
    private final int updatedDirectionsNumber;
    private final Airline airline;

    @JsonCreator
    public DirectionRefreshDetails(@JsonProperty("id") long id,
                                   @JsonProperty("updatingTime") LocalDateTime updatingTime,
                                   @JsonProperty("updatedDirectionsNumber") int updatedDirectionsNumber,
                                   @JsonProperty("airline") Airline airline) {
        this.id = id;
        this.updatingTime = updatingTime;
        this.updatedDirectionsNumber = updatedDirectionsNumber;
        this.airline = airline;

    }

    public Airline getAirline() {
        return airline;
    }

    public long getId() {
        return id;
    }

    public LocalDateTime getUpdatingTime() {
        return updatingTime;
    }

    public int getUpdatedDirectionsNumber() {
        return updatedDirectionsNumber;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.JSON_STYLE);
    }
}
