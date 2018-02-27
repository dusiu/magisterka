package pl.edu.uj.dusinski;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
//@RequestMapping("/flights")
public class FlightDetailController {

    private final FlightDataProviderService flightDataProviderService;

    @Autowired
    public FlightDetailController(FlightDataProviderService flightDataProviderService) {
        this.flightDataProviderService = flightDataProviderService;
    }

    @RequestMapping("/")
    public String possibleFlights(Model model) {
        model.addAttribute("flyFrom", flightDataProviderService.getDirectionFromWhereFlyTo());
        return "index";
    }

    @RequestMapping("/flyFrom/{code}")
    public String flyToDirections(@PathVariable("code") String code, Model model) {
        model.addAttribute("flyTo", flightDataProviderService.getFlyightsForDirection(code));
        return "flyTo";
    }

}
