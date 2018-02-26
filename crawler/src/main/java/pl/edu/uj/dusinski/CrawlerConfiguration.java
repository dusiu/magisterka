package pl.edu.uj.dusinski;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CrawlerConfiguration {

    @Value("${web.driver.instances}")
    int webDriverInstances;

    @Bean
    public WebDriverMangerService webDriverMangerService() {
        return new WebDriverMangerService(webDriverInstances);
    }
}
