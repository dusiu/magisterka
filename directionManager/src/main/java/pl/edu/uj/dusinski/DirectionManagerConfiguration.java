package pl.edu.uj.dusinski;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DirectionManagerConfiguration {

    @Value("${web.driver.instances}")
    private int webDriverInstances;

    @Bean
    public WebDriverMangerService webDriverMangerService() {
        return new WebDriverMangerService(webDriverInstances);
    }

}
