package edu.soumyadeep.flightbooking.service.impl.amadeus;

import edu.soumyadeep.flightbooking.model.FlightOfferCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class AmadeusFlightMapper {

    private static final Logger log = LoggerFactory.getLogger(AmadeusFlightMapper.class);

    public List<Map<String, Object>> mapFlights(List<Map<String, Object>> apiData,
                                                String cabin, String tripDirection) {
        List<Map<String, Object>> flights = new ArrayList<>();

        for (Map<String, Object> offer : apiData) {
            try {
                Map<String, Object> price = (Map<String, Object>) offer.get("price");
                List<Map<String, Object>> itineraries = (List<Map<String, Object>>) offer.get("itineraries");
                if (itineraries == null || itineraries.isEmpty()) continue;

                Map<String, Object> itinerary = itineraries.get(0);
                List<Map<String, Object>> segments = (List<Map<String, Object>>) itinerary.get("segments");
                if (segments == null || segments.isEmpty()) continue;

                // Get first departure and FINAL arrival (not first segment arrival)
                Map<String, Object> firstSegment = segments.get(0);
                Map<String, Object> lastSegment = segments.get(segments.size() - 1);

                Map<String, Object> dep = (Map<String, Object>) firstSegment.get("departure");
                Map<String, Object> arr = (Map<String, Object>) lastSegment.get("arrival");

                if (dep == null || arr == null) continue;

                String id = offer.get("id").toString() + "_" + tripDirection;

                Map<String, Object> flight = new HashMap<>();
                flight.put("offerId", id);
                flight.put("origin", dep.get("iataCode"));
                flight.put("destination", arr.get("iataCode"));
                flight.put("price", price.get("total"));
                flight.put("currency", price.get("currency"));
                flight.put("cabin", cabin);
                flight.put("tripDirection", tripDirection);

                flights.add(flight);

            } catch (Exception ex) {
                log.error("Error mapping flight data: {}", ex.getMessage());
            }
        }

        log.info("Mapped {} flights (direction: {})", flights.size(), tripDirection);
        return flights;
    }

    public FlightOfferCache toEntity(Map<String, Object> flight) {
        return FlightOfferCache.builder()
                .offerId(flight.get("offerId").toString())
                .origin(flight.get("origin").toString())
                .destination(flight.get("destination").toString())
                .price(Double.parseDouble(flight.get("price").toString()))
                .currency(flight.get("currency").toString())
                .cabin(flight.get("cabin").toString())
                .tripDirection(flight.get("tripDirection").toString())
                .build();
    }
}