package pl.edu.uj.dusinski.dao;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        DirectionRefreshDetails that = (DirectionRefreshDetails) o;

        return new EqualsBuilder()
                .append(id, that.id)
                .append(updatedDirectionsNumber, that.updatedDirectionsNumber)
                .append(updatingTime, that.updatingTime)
                .append(airline, that.airline)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(id)
                .append(updatingTime)
                .append(updatedDirectionsNumber)
                .append(airline)
                .toHashCode();
    }
}
