package edu.soumyadeep.flightbooking.controller;

import edu.soumyadeep.flightbooking.dto.FlightSearchRequest;
import edu.soumyadeep.flightbooking.service.FlightService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/flights")
public class FlightController {
    private final FlightService flightService;

    public FlightController(FlightService flightService) { this.flightService = flightService; }

    @PostMapping("/search")
    public ResponseEntity<List<Map<String,Object>>> search(@Valid @RequestBody FlightSearchRequest req) {
        return ResponseEntity.ok(flightService.searchFlights(req));
    }
}
