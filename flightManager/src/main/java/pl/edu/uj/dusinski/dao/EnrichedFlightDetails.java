package pl.edu.uj.dusinski.dao;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class EnrichedFlightDetails extends FlightDetails {

    private final double plnPrice;
    private final String flightUrl;
    private final AirportDetails fromAirport;
    private final AirportDetails toAirport;

    public EnrichedFlightDetails(FlightDetails flightDetails, double plnPrice, String flightUrl, AirportDetails fromAirport, AirportDetails toAirport) {
        super(flightDetails.getId(), flightDetails.getFlyDate(), flightDetails.getDirection(), flightDetails.getOriginalPrice(), flightDetails.getCurrency());
        this.plnPrice = plnPrice;
        this.flightUrl = flightUrl;
        this.fromAirport = fromAirport;
        this.toAirport = toAirport;
    }

    public double getPlnPrice() {
        return plnPrice;
    }

    public String getFlightUrl() {
        return flightUrl;
    }

    public AirportDetails getFromAirport() {
        return fromAirport;
    }

    public AirportDetails getToAirport() {
        return toAirport;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.JSON_STYLE);
    }
}
