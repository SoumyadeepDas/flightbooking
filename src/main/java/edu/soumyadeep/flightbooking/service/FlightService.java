package edu.soumyadeep.flightbooking.service;


import edu.soumyadeep.flightbooking.dto.FlightSearchRequest;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface FlightService {
    List<Map<String, Object>> searchFlights(FlightSearchRequest req);
    Optional<Map<String, Object>> getOfferById(String offerId);
}
