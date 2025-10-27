package edu.soumyadeep.flightbooking.service.impl.amadeus;
import edu.soumyadeep.flightbooking.model.FlightOfferCache;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
@Slf4j
public class AmadeusFlightMapper {

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

                // Extract and store the departure date (for validation later)
                if (dep.get("at") != null) {
                    String fullDateTime = dep.get("at").toString(); // e.g., 2025-11-25T10:30:00
                    String onlyDate = fullDateTime.split("T")[0];    // -> 2025-11-25
                    flight.put("departDate", onlyDate);
                }

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
                // ✅ Persist depart date too if available
                .departDate(flight.get("departDate") != null ? flight.get("departDate").toString() : null)
                .build();
    }
}