package pl.edu.uj.dusinski.dao;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.springframework.data.annotation.Id;

import javax.annotation.concurrent.Immutable;

@Immutable
public class Direction {

    @Id
    private final String id;
    private final String fromCode;
    private final String toCode;
    private final Airline airline;

    @JsonCreator
    public Direction(@JsonProperty("id") String id,
                     @JsonProperty("fromCode") String fromCode,
                     @JsonProperty("toCode") String toCode,
                     @JsonProperty("airline") Airline airline) {
        this.id = id;
        this.fromCode = fromCode;
        this.toCode = toCode;
        this.airline = airline;
    }

    public Airline getAirline() {
        return airline;
    }

    public String getId() {
        return id;
    }

    public String getFromCode() {
        return fromCode;
    }

    public String getToCode() {
        return toCode;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.JSON_STYLE);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        Direction direction = (Direction) o;

        return new EqualsBuilder()
                .append(id, direction.id)
                .append(fromCode, direction.fromCode)
                .append(toCode, direction.toCode)
                .append(airline, direction.airline)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(id)
                .append(fromCode)
                .append(toCode)
                .append(airline)
                .toHashCode();
    }
}
