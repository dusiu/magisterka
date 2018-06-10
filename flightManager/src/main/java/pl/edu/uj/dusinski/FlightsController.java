package pl.edu.uj.dusinski;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import pl.edu.uj.dusinski.dao.Airline;
import pl.edu.uj.dusinski.dao.AirportDetails;
import pl.edu.uj.dusinski.dao.FlightDetailsRequest;
import pl.edu.uj.dusinski.services.FlightDataChooserService;
import pl.edu.uj.dusinski.services.FlightDataProviderService;

import java.util.List;

@Controller
public class FlightsController {
    private static final Logger Log = LoggerFactory.getLogger(FlightsController.class);

    private final FlightDataProviderService flightDataProviderService;
    private final FlightDataChooserService flightDataChooserService;
    private final AirportDetails takeMeAnyway = new AirportDetails("Take Me Anyway", "", "", "ANYWAY", Airline.UNKNOWN);

    @Autowired
    public FlightsController(FlightDataProviderService flightDataProviderService,
                             FlightDataChooserService flightDataChooserService) {
        this.flightDataProviderService = flightDataProviderService;
        this.flightDataChooserService = flightDataChooserService;
    }

    @RequestMapping("/")
    public String getPossibleFlights(Model model) {
        List<AirportDetails> fromWhereFlyTo = flightDataProviderService.getDirectionFromWhereFlyTo();
        model.addAttribute("flyFromAirports", fromWhereFlyTo);
        Log.info("Returning {} directions from which you can fly to", fromWhereFlyTo.size());
        return "index";
    }

    @RequestMapping("/flyFrom/{id}")
    public String getFlyToDirections(@PathVariable("id") String id, Model model) {
        String code = id.substring(0, 3);
        List<AirportDetails> flightsForDirection = flightDataProviderService.getAirportsForDirection(code);
        AirportDetails currentAirport = flightDataProviderService.getAirportDetails(id);
        if (flightsForDirection.isEmpty() || currentAirport == null) {
            model.addAttribute("error", "Error during getting directions, probably database manager is not running");
        } else {
            flightsForDirection.add(0, takeMeAnyway);
            model.addAttribute("directions", flightsForDirection);
            model.addAttribute("currentAirport", currentAirport);
            model.addAttribute("request", FlightDetailsRequest.createNewInstance());
        }
        Log.info("Creating request for fly from {}", id);
        return "flightDetails";
    }

    @RequestMapping(value = "/flyTo", method = RequestMethod.POST, consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public String getFlyToDetails(@ModelAttribute("request") FlightDetailsRequest flightDetailsRequest, Model model) {
        model.addAttribute("flightData", flightDataChooserService.getBestDealsForRequest(flightDetailsRequest));
        Log.info("Returning flight details for request {}", flightDetailsRequest);
        if (flightDetailsRequest.isBothWay()) {
            return "flightDataBothWay";
        }
        return "flightDataSingleWay";
    }

}
