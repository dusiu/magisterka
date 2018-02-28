package pl.edu.uj.dusinski.controllers;

import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import pl.edu.uj.dusinski.dao.AirportDetails;
import pl.edu.uj.dusinski.dao.FlightDetails;
import pl.edu.uj.dusinski.jpa.AirportDetailsRepository;
import pl.edu.uj.dusinski.jpa.FlightDetailsRepository;

import java.util.List;
import java.util.Objects;
import java.util.function.BiPredicate;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/flights")
public class FlightDetailsController {
    private static final Logger Log = LoggerFactory.getLogger(FlightDetailsController.class);

    private final Gson gson = new Gson();
    private final FlightDetailsRepository flightDetailsRepository;
    private final AirportDetailsRepository airportDetailsRepository;
    private final String anyway = "ANYWAY";
    private final BiPredicate<FlightDetails, String> goToCodeOrAnyway = (v, toCode) -> v.getDirection().getToCode().equals(toCode) || anyway.equals(toCode);

    @Autowired
    public FlightDetailsController(FlightDetailsRepository flightDetailsRepository,
                                   AirportDetailsRepository airportDetailsRepository) {
        this.flightDetailsRepository = flightDetailsRepository;
        this.airportDetailsRepository = airportDetailsRepository;
    }

    @RequestMapping("/flyFrom")
    @ResponseBody
    public String findWhereFromCanFly() {
        Log.info("Returning fly from directions");
        List<FlightDetails> flightDetails = flightDetailsRepository.findAll();
        return gson.toJson(
                flightDetails
                        .stream()
                        .map(v -> airportDetailsRepository.findByCode(v.getDirection().getFromCode()))
                        .filter(Objects::nonNull)
                        .distinct()
                        .collect(Collectors.toList()));
    }

    @RequestMapping("/getDirections/{direction}")
    @ResponseBody
    public String findDirectionsToFly(@PathVariable("direction") String direction) {
        List<FlightDetails> flightDetails = flightDetailsRepository.findAll();
        Log.info("Returning fly to direction for {}", direction);
        return gson.toJson(
                flightDetails
                        .stream()
                        .filter(v -> v.getDirection().getFromCode().equals(direction))
                        .map(v -> airportDetailsRepository.findByCode(v.getDirection().getToCode()))
                        .filter(Objects::nonNull)
                        .distinct()
                        .collect(Collectors.toList()));
    }

    @RequestMapping("/getAllAirports")
    @ResponseBody
    public String getAirportDetails() {
        List<AirportDetails> airports = airportDetailsRepository.findAll();
        Log.info("Returning all airport details");
        return gson.toJson(airports.stream().filter(Objects::nonNull).distinct().collect(Collectors.toList()));
    }

    @RequestMapping("/flightDetails/{from}/{to}")
    @ResponseBody
    public String getAirportDetails(@PathVariable("from") String from, @PathVariable("to") String to) {
        List<FlightDetails> flightDetails = flightDetailsRepository.findAll()
                .stream()
                .filter(v -> v.getDirection().getFromCode().equals(from))
                .filter(v -> goToCodeOrAnyway.test(v, to))
                .collect(Collectors.toList());
        Log.info("Returning flight details from {} to {}", from, to);
        return gson.toJson(flightDetails);
    }
}
