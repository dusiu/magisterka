package pl.edu.uj.dusinski;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import pl.edu.uj.dusinski.config.JmsConfiguration;

@SpringBootApplication
@ComponentScan(excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = JmsConfiguration.class))
public class FlightManagerApplication {

    public static void main(String[] args) {
        SpringApplication.run(FlightManagerApplication.class);
    }
}
