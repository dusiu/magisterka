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
import java.util.stream.Collectors;

@Controller
@RequestMapping("/airport")
public class AirportProvider {
    private static final Logger LOGGER = LoggerFactory.getLogger(AirportProvider.class);

    private final AirportDetailsRepository airportDetailsRepository;
    private final Gson gson = new Gson();


    public AirportProvider(AirportDetailsRepository airportDetailsRepository) {
        this.airportDetailsRepository = airportDetailsRepository;
    }


    @RequestMapping("/allairports")
    @ResponseBody
    public String findAllReports() {
        List<AirportDetails> allAirports = airportDetailsRepository.findAll();
        LOGGER.info("Returning {} airports details", allAirports.size());
        return gson.toJson(allAirports);
    }

}
