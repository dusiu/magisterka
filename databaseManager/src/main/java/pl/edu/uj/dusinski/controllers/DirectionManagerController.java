package pl.edu.uj.dusinski.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import pl.edu.uj.dusinski.dao.Airline;
import pl.edu.uj.dusinski.dao.DirectionRefreshDetails;
import pl.edu.uj.dusinski.jpa.DirectionRefreshDetailsRepository;
import pl.edu.uj.dusinski.services.DirectionUpdaterService;

import java.time.LocalDateTime;
import java.util.Optional;

@Controller
@RequestMapping("/directionManager")
public class DirectionManagerController {
    private static final Logger Log = LoggerFactory.getLogger(DirectionManagerController.class);

    private final DirectionRefreshDetailsRepository refreshDetailsRepository;
    private final DirectionUpdaterService directionUpdaterService;
    private final DirectionRefreshDetails emptyDirectionRefreshDetails = new DirectionRefreshDetails(LocalDateTime.now().minusMonths(1), 0, Airline.UNKNOWN);

    @Autowired
    public DirectionManagerController(DirectionRefreshDetailsRepository refreshDetailsRepository,
                                      DirectionUpdaterService directionUpdaterService) {
        this.refreshDetailsRepository = refreshDetailsRepository;
        this.directionUpdaterService = directionUpdaterService;
    }

    @RequestMapping(value = "/lastUpdatedTimeRyanair", produces = "application/json")
    @ResponseBody
    public String directionLatUpdatedTimeRyanair() {
        Log.info("Received last updated time request");
        Optional<DirectionRefreshDetails> topById = refreshDetailsRepository.findTopByAirlineOrderByIdDesc(Airline.RYANAIR);
        if (!topById.isPresent()) {
            return emptyDirectionRefreshDetails.toString();
        }
        return topById.get().toString();
    }

    @RequestMapping(value = "/lastUpdatedTimeWizzair", produces = "application/json")
    @ResponseBody
    public String directionLatUpdatedTimeWizzair() {
        Log.info("Received last updated time request");
        Optional<DirectionRefreshDetails> topById = refreshDetailsRepository.findTopByAirlineOrderByIdDesc(Airline.WIZZAIR);
        if (!topById.isPresent()) {
            return emptyDirectionRefreshDetails.toString();
        }
        return topById.get().toString();
    }

    @RequestMapping(value = "/updateNewDirections/{airline}")
    @ResponseBody
    public String saveNewDirections(@PathVariable("airline") Airline airline) {
        Log.info("Updating directions in database for {}", airline);
        directionUpdaterService.updateDirectionsInDatabase(airline);
        return "ok";
    }
}
