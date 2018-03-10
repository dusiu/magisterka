package pl.edu.uj.dusinski;

import java.time.LocalDate;
import java.util.List;

public class FlightDetailsData {

    private final List<Fare> fares;

    public FlightDetailsData(List<Fare> fares) {
        this.fares = fares;
    }

    public List<Fare> getFares() {
        return fares;
    }

    public static class Fare {
        private final FlightData outbound;
        private final SummaryData summary;

        public Fare(FlightData outbound, SummaryData summary) {
            this.outbound = outbound;
            this.summary = summary;
        }

        public FlightData getOutbound() {
            return outbound;
        }

        public SummaryData getSummary() {
            return summary;
        }
    }

    public static class SummaryData {
        private final PriceData price;

        public SummaryData(PriceData price) {
            this.price = price;
        }

        public PriceData getPrice() {
            return price;
        }
    }

    public static class PriceData {
        private final double value;
        private final String currencyCode;

        public PriceData(double value, String currencyCode) {
            this.value = value;
            this.currencyCode = currencyCode;
        }

        public double getValue() {
            return value;
        }

        public String getCurrencyCode() {
            return currencyCode;
        }
    }

    public static class FlightData {
        private final AirportData departureAirport;
        private final AirportData arrivalAirport;
        private final String departureDate;

        public FlightData(AirportData departureAirport, AirportData arrivalAirport, String departureDate) {
            this.departureAirport = departureAirport;
            this.arrivalAirport = arrivalAirport;
            this.departureDate = departureDate;
        }

        public AirportData getDepartureAirport() {
            return departureAirport;
        }

        public AirportData getArrivalAirport() {
            return arrivalAirport;
        }

        public LocalDate getDepartureDate() {
            return LocalDate.parse(departureDate.substring(0, departureDate.indexOf("T")));
        }
    }

    private static class AirportData {
        private final String iataCode;

        private AirportData(String iataCode) {
            this.iataCode = iataCode;
        }

        public String getIataCode() {
            return iataCode;
        }
    }
}
