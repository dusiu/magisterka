package pl.edu.uj.dusinski.controllers;

import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import pl.edu.uj.dusinski.dao.FlightDetails;
import pl.edu.uj.dusinski.jpa.FlightDetailsRepository;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/flights")
public class FlightDetailsController {
    private static final Logger Log = LoggerFactory.getLogger(FlightDetailsController.class);

    private final Gson gson = new Gson();
    private final FlightDetailsRepository flightDetailsRepository;

    @Autowired
    public FlightDetailsController(FlightDetailsRepository flightDetailsRepository) {
        this.flightDetailsRepository = flightDetailsRepository;
    }

    @RequestMapping("/flyFrom")
    @ResponseBody
    public String findWhereFromCanFly() {
        Log.info("Returning fly from directions");
        List<FlightDetails> flightDetails = flightDetailsRepository.findAll();
        return gson.toJson(
                flightDetails
                        .stream()
                        .map(v -> v.getDirection().getFromCode())
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
                        .map(v -> v.getDirection().getToCode())
                        .distinct()
                        .collect(Collectors.toList()));
    }
}
