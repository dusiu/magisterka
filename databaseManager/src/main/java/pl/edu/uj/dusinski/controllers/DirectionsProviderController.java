package pl.edu.uj.dusinski.controllers;

import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import pl.edu.uj.dusinski.dao.Airline;
import pl.edu.uj.dusinski.dao.Direction;
import pl.edu.uj.dusinski.jpa.DirectionRepository;

import java.util.Collections;
import java.util.List;

@Controller
@RequestMapping("/directions")
public class DirectionsProviderController {
    private static final Logger Log = LoggerFactory.getLogger(DirectionsProviderController.class);

    private final DirectionRepository directionRepository;
    private final Gson gson = new Gson();

    public DirectionsProviderController(DirectionRepository directionRepository) {
        this.directionRepository = directionRepository;
    }

    @RequestMapping(value = "/allDirections/{airline}", produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String getAllDirectionFor(@PathVariable("airline") Airline airline) {
        List<Direction> directions = directionRepository.findAllByAirline(airline);
        if (directions.isEmpty()) {
            return gson.toJson(Collections.emptyList());
        }
        Log.info("Returning {} directions", directions.size());
        return gson.toJson(directions);
    }

}
