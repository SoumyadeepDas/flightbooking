package edu.soumyadeep.flightbooking.service.impl.amadeus;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class AmadeusApiClient {

    private static final Logger log = LoggerFactory.getLogger(AmadeusApiClient.class);

    @Value("${amadeus.api.base-url}")
    private String baseUrl;

    public List<Map<String, Object>> fetchFlights(String origin, String destination, LocalDate date,
                                                  String cabin, String token, String tripDirection) {

        WebClient client = WebClient.builder()
                .baseUrl(baseUrl)
                .defaultHeader("Authorization", "Bearer " + token)
                .exchangeStrategies(defaultExchangeStrategy())
                .build();

        String uri = String.format(
                "/v2/shopping/flight-offers?originLocationCode=%s&destinationLocationCode=%s&departureDate=%s&adults=1&travelClass=%s",
                origin, destination, date, cabin
        );

        Map<String, Object> response = client.get()
                .uri(uri)
                .retrieve()
                .bodyToMono(Map.class)
                .onErrorResume(e -> {
                    log.error("Error calling Amadeus API for {} -> {}: {}", origin, destination, e.getMessage());
                    return Mono.empty();
                })
                .block();

        if (response == null || !response.containsKey("data")) {
            log.warn("No flight data returned from Amadeus for {} -> {}", origin, destination);
            return List.of();
        }

        List<Map<String, Object>> allFlights = (List<Map<String, Object>>) response.get("data");
        List<Map<String, Object>> filteredFlights = new ArrayList<>();

        for (Map<String, Object> offer : allFlights) {
            try {
                List<Map<String, Object>> itineraries = (List<Map<String, Object>>) offer.get("itineraries");
                if (itineraries == null || itineraries.isEmpty()) continue;

                Map<String, Object> firstItinerary = itineraries.get(0);
                List<Map<String, Object>> segments = (List<Map<String, Object>>) firstItinerary.get("segments");
                if (segments == null || segments.isEmpty()) continue;

                // take *final* segment to confirm actual arrival
                Map<String, Object> lastSegment = segments.get(segments.size() - 1);
                Map<String, Object> arrival = (Map<String, Object>) lastSegment.get("arrival");
                if (arrival == null || arrival.get("iataCode") == null) continue;

                String arrivalCode = arrival.get("iataCode").toString();
                if (!destination.equalsIgnoreCase(arrivalCode)) {
                    log.debug("Skipping offer {} because final arrival '{}' != requested '{}'",
                            offer.get("id"), arrivalCode, destination);
                    continue; // skip incorrect destination
                }

                filteredFlights.add(offer);

            } catch (Exception e) {
                log.warn("Error parsing flight offer: {}", e.getMessage());
            }
        }

        log.info("Amadeus API: Fetched {} valid flights for {} -> {} (Filtered from {})",
                filteredFlights.size(), origin, destination, allFlights.size());

        return filteredFlights;
    }

    private ExchangeStrategies defaultExchangeStrategy() {
        return ExchangeStrategies.builder()
                .codecs(cfg -> cfg.defaultCodecs().maxInMemorySize(10 * 1024 * 1024))
                .build();
    }
}