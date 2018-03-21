package pl.edu.uj.dusinski.controllers;

import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import pl.edu.uj.dusinski.dao.Direction;
import pl.edu.uj.dusinski.dao.FlightDetails;
import pl.edu.uj.dusinski.dao.FlightDetailsBothWay;
import pl.edu.uj.dusinski.dao.FlightDetailsRequest;
import pl.edu.uj.dusinski.jpa.AirportDetailsRepository;
import pl.edu.uj.dusinski.jpa.DirectionRepository;
import pl.edu.uj.dusinski.jpa.FlightDetailsRepository;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/flights")
public class FlightDetailsController {
    private static final Logger Log = LoggerFactory.getLogger(FlightDetailsController.class);

    private final Gson gson = new Gson();
    private final FlightDetailsRepository flightDetailsRepository;
    private final AirportDetailsRepository airportDetailsRepository;
    private final DirectionRepository directionRepository;
    private final String anyway = "ANYWAY";

    @Autowired
    public FlightDetailsController(FlightDetailsRepository flightDetailsRepository,
                                   AirportDetailsRepository airportDetailsRepository,
                                   DirectionRepository directionRepository) {
        this.flightDetailsRepository = flightDetailsRepository;
        this.airportDetailsRepository = airportDetailsRepository;
        this.directionRepository = directionRepository;
    }

    @RequestMapping(value = "/flyFrom", produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String findWhereFromCanFly() {
        Log.info("Returning fly from directions");
        return gson.toJson(
                flightDetailsRepository.findByDirectionIn(directionRepository.findAll()).stream()
                        .map(v -> v.getDirection().getFromCode())
                        .distinct()
                        .map(v -> airportDetailsRepository.findByCode(v).get(0))
                        .collect(Collectors.toList()));
    }

    @RequestMapping(value = "/getDirections/{direction}", produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String findDirectionsToFly(@PathVariable("direction") String direction) {
        List<FlightDetails> flightDetails = flightDetailsRepository.findAll();
        Log.info("Returning fly to direction for {}", direction);
        return gson.toJson(
                flightDetails
                        .stream()
                        .filter(v -> v.getDirection() != null && v.getDirection().getFromCode().equals(direction))
                        .map(FlightDetails::getDirection)
                        .distinct()
                        .collect(Collectors.toList()));
    }

    @RequestMapping(value = "/flightDetails", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String getAirportDetails(@RequestBody FlightDetailsRequest request) {

        List<Direction> directions;
        if (anyway.equals(request.getToCode())) {
            directions = directionRepository.findByFromCode(request.getFromCode());
        } else {
            directions = directionRepository.findByFromCodeAndToCode(request.getFromCode(), request.getToCode());
        }

        List<FlightDetails> flightDetails = flightDetailsRepository.findByDirectionIn(directions);
        if (!request.isBothWay()) {
            return gson.toJson(flightDetails);
        }

        List<FlightDetailsBothWay> bothWayFlightsMap = flightDetails.stream()
                .sorted(Comparator.comparingDouble(FlightDetails::getOriginalPrice))
                .limit(100)
                .map(v -> flightDetailsRepository.findTopByDirectionInAndFlyDateBetweenOrderByOriginalPrice(
                        findOppositeDirection(v), prepareStartDate(v, request), prepareEndDate(v, request))
                        .map(toFlight -> new FlightDetailsBothWay(v, toFlight)).orElse(null))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        Log.info("Returning flight details from {} to {}", request.getFromCode(), request.getToCode());
        return gson.toJson(bothWayFlightsMap);
    }

    private LocalDate prepareStartDate(FlightDetails v, FlightDetailsRequest request) {
        return v.getFlyDate().plusDays(request.getMinDaysToStay() - 1);
    }

    private LocalDate prepareEndDate(FlightDetails v, FlightDetailsRequest request) {
        return v.getFlyDate().plusDays(request.getMaxDaysToStay() + 1);
    }

    private List<Direction> findOppositeDirection(FlightDetails flightDetails) {
        return directionRepository.findByFromCodeAndToCode(flightDetails.getDirection().getToCode(),
                flightDetails.getDirection().getFromCode());
    }


}
