package pl.edu.uj.dusinski.controllers;

import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import pl.edu.uj.dusinski.dao.AirportDetails;
import pl.edu.uj.dusinski.jpa.AirportDetailsRepository;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/airports")
public class AirportsProviderController {
    private static final Logger Log = LoggerFactory.getLogger(AirportsProviderController.class);

    private final AirportDetailsRepository airportDetailsRepository;
    private final Gson gson = new Gson();


    public AirportsProviderController(AirportDetailsRepository airportDetailsRepository) {
        this.airportDetailsRepository = airportDetailsRepository;
    }

    @RequestMapping(value = "/getAllAirports", produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String getAirportDetails() {
        List<AirportDetails> airports = airportDetailsRepository.findAll();
        Log.info("Returning all airport details");
        return gson.toJson(airports.stream().filter(Objects::nonNull).distinct().collect(Collectors.toList()));
    }
}
