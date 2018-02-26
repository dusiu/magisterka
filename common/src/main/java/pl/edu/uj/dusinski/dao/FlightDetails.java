package pl.edu.uj.dusinski.dao;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.springframework.data.annotation.Id;

import javax.annotation.concurrent.Immutable;
import java.time.LocalDate;

@Immutable
public class FlightDetails {

    @Id
    private final String id;
    @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonSerialize(using = LocalDateSerializer.class)
    private final LocalDate flyDate;
    private final Direction direction;
    private final double originalPrice;
    private final String currency;

    @JsonCreator
    public FlightDetails(@JsonProperty("id") String id,
                         @JsonProperty("flyDate") LocalDate flyDate,
                         @JsonProperty("direction") Direction direction,
                         @JsonProperty("originalPrice") double originalPrice,
                         @JsonProperty("currency") String currency) {
        this.id = id;
        this.flyDate = flyDate;
        this.direction = direction;
        this.originalPrice = originalPrice;
        this.currency = currency;
    }

    public String getId() {
        return id;
    }

    public LocalDate getFlyDate() {
        return flyDate;
    }

    public Direction getDirection() {
        return direction;
    }

    public double getOriginalPrice() {
        return originalPrice;
    }

    public String getCurrency() {
        return currency;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.JSON_STYLE);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        FlightDetails that = (FlightDetails) o;

        return new EqualsBuilder()
                .append(originalPrice, that.originalPrice)
                .append(id, that.id)
                .append(flyDate, that.flyDate)
                .append(direction, that.direction)
                .append(currency, that.currency)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(id)
                .append(flyDate)
                .append(direction)
                .append(originalPrice)
                .append(currency)
                .toHashCode();
    }
}
