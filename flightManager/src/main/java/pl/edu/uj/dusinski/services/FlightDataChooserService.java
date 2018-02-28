package pl.edu.uj.dusinski.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.edu.uj.dusinski.dao.*;

import java.util.Comparator;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class FlightDataChooserService {

    private final FlightDataProviderService flightDataProviderService;
    private final PlnCurrencyConverterService plnConverterService;
    private final FlightsUrlGeneratorService urlGeneratorService;

    @Autowired
    public FlightDataChooserService(FlightDataProviderService flightDataProviderService,
                                    PlnCurrencyConverterService plnConverterService,
                                    FlightsUrlGeneratorService urlGeneratorService) {
        this.flightDataProviderService = flightDataProviderService;
        this.plnConverterService = plnConverterService;
        this.urlGeneratorService = urlGeneratorService;
    }

    public Set<EnrichedFlightDetails> getBestDealsForRequest(FlightDetailsRequest flightDetailsRequest) {
        Set<FlightDetails> detailsForRequest = flightDataProviderService.getFlightDetailsForRequest(flightDetailsRequest);
        return detailsForRequest.stream()
                .map(v -> new EnrichedFlightDetails(v, plnConverterService.apply(v.getCurrency(), v.getOriginalPrice()), urlGeneratorService.apply(v),
                        getAirportBasedOnCode(v.getDirection().getFromCode(), v.getDirection().getAirline()), getAirportBasedOnCode(v.getDirection().getToCode(), v.getDirection().getAirline())))
                .sorted(Comparator.comparingDouble(EnrichedFlightDetails::getPlnPrice))
                .collect(Collectors.toSet());
    }

    private AirportDetails getAirportBasedOnCode(String fromCode, Airline airline) {
        return flightDataProviderService.getAirportDetails(fromCode+airline.getValue().toLowerCase());
    }

}
