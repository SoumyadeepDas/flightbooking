package edu.soumyadeep.flightbooking;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@Slf4j
@SpringBootApplication
public class FlightbookingApplication {

    public static void main(String[] args) {
        log.info("FlightbookingApplication started...");
        SpringApplication.run(FlightbookingApplication.class, args);
    }

}
