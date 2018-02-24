package pl.edu.uj.dusinski;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.jms.annotation.EnableJms;

@SpringBootApplication
@EnableJms
public class ApplicationMain {

    public static void main(String[] args) {
        SpringApplication.run(ApplicationMain.class);
    }

}
