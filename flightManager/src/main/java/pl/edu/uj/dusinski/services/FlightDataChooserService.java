package pl.edu.uj.dusinski.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import pl.edu.uj.dusinski.dao.*;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class FlightDataChooserService {

    private final FlightDataProviderService flightDataProviderService;
    private final Function<FlightDetails, EnrichedFlightDetails> flightEnricher;
    private final int maxShownFlight;

    @Autowired
    public FlightDataChooserService(FlightDataProviderService flightDataProviderService,
                                    PlnCurrencyConverterService plnConverterService,
                                    FlightsUrlGeneratorService urlGeneratorService,
                                    @Value("${max.shown.flight:50}") int maxShownFlight) {
        this.maxShownFlight = maxShownFlight;
        this.flightDataProviderService = flightDataProviderService;
        this.flightEnricher = v -> new EnrichedFlightDetails(v,
                plnConverterService.apply(v.getCurrency(), v.getOriginalPrice()),
                urlGeneratorService.apply(v),
                getAirportBasedOnCode(v.getDirection().getFromCode(), v.getDirection().getAirline()),
                getAirportBasedOnCode(v.getDirection().getToCode(), v.getDirection().getAirline()));
    }

    public Object getBestDealsForRequest(FlightDetailsRequest flightDetailsRequest) {
        if (flightDetailsRequest.isBothWay()) {
            List<FlightDetailsBothWay> bothWaysFlightDetails = flightDataProviderService.getBothWaysFlightDetails(flightDetailsRequest);
            return bothWaysFlightDetails.stream()
                    .map(v -> Map.entry(flightEnricher.apply(v.getFrom()), getCheapestFlight(v.getTo())))
                    .map(v -> new EnrichedFlightDetailsGroup(v.getKey(), v.getValue()))
                    .sorted(Comparator.comparingDouble(EnrichedFlightDetailsGroup::getTotalPlnPrice))
                    .limit(maxShownFlight)
                    .collect(Collectors.toList());
        } else {
            Set<FlightDetails> detailsForRequest = flightDataProviderService.getOneWayFlightDetails(flightDetailsRequest);
            return detailsForRequest.stream()
                    .map(flightEnricher)
                    .sorted(Comparator.comparingDouble(EnrichedFlightDetails::getPlnPrice))
                    .limit(maxShownFlight)
                    .collect(Collectors.toList());
        }
    }

    private EnrichedFlightDetails getCheapestFlight(List<FlightDetails> value) {
        return value.stream()
                .map(flightEnricher)
                .sorted(Comparator.comparingDouble(EnrichedFlightDetails::getPlnPrice))
                .findFirst()
                .get();
    }

    private AirportDetails getAirportBasedOnCode(String fromCode, Airline airline) {
        return flightDataProviderService.getAirportDetails(fromCode + airline.getValue());
    }

}
