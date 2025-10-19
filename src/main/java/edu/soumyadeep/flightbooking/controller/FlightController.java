package edu.soumyadeep.flightbooking.controller;

import edu.soumyadeep.flightbooking.dto.FlightSearchRequest;
import edu.soumyadeep.flightbooking.service.FlightService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

import static edu.soumyadeep.flightbooking.constant.EndpointConstant.FLIGHT_PATH;
import static edu.soumyadeep.flightbooking.constant.EndpointConstant.FLIGHT_SEARCH;

@RestController
@RequestMapping(FLIGHT_PATH)
@RequiredArgsConstructor
public class FlightController {
    private final FlightService flightService;

    @PostMapping(FLIGHT_SEARCH)
    public ResponseEntity<List<Map<String,Object>>> search(@Valid @RequestBody final FlightSearchRequest req) {
        return ResponseEntity.ok(flightService.searchFlights(req));
    }
}
