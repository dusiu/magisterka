package pl.edu.uj.dusinski.dao;

import org.springframework.data.annotation.Id;

import javax.annotation.concurrent.Immutable;

@Immutable
public class AirportDetails {

    @Id
    private final String code;
    private final String name;
    private final String fullName;
    private final String description;
    private final Airline airline;

    public AirportDetails(String name, String fullName, String description, String code, Airline airline) {
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
}
