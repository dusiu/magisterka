package pl.edu.uj.dusinski.dao;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.springframework.data.annotation.Id;

import javax.annotation.concurrent.Immutable;

@Immutable
public class AirportDetails {

    @Id
    private final String id;
    private final String code;
    private final String name;
    private final String fullName;
    private final String description;
    private final Airline airline;

    @JsonCreator
    public AirportDetails(@JsonProperty("name") String name,
                          @JsonProperty("full") String fullName,
                          @JsonProperty("description") String description,
                          @JsonProperty("code") String code,
                          @JsonProperty("airline") Airline airline) {
        this.id = code + airline.getValue();
        this.name = name;
        this.fullName = fullName;
        this.description = description;
        this.code = code;
        this.airline = airline;
    }

    public Airline getAirline() {
        return airline;
    }

    public String getName() {
        return name;
    }

    public String getFullName() {
        return fullName;
    }

    public String getDescription() {
        return description;
    }

    public String getCode() {
        return code;
    }

    public String getId() {
        return id;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.JSON_STYLE);
    }
}
