package edu.soumyadeep.flightbooking.service.impl;

import edu.soumyadeep.flightbooking.dto.FlightSearchRequest;
import edu.soumyadeep.flightbooking.model.FlightOfferCache;
import edu.soumyadeep.flightbooking.repository.FlightOfferCacheRepository;
import edu.soumyadeep.flightbooking.service.FlightService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.*;

@Service
public class AmadeusFlightServiceImpl implements FlightService {

    private static final Logger log = LoggerFactory.getLogger(AmadeusFlightServiceImpl.class);

    private final FlightOfferCacheRepository offerRepo;

    public AmadeusFlightServiceImpl(FlightOfferCacheRepository offerRepo) {
        this.offerRepo = offerRepo;
    }

    @Value("${amadeus.api.base-url}")
    private String baseUrl;

    @Value("${amadeus.api.key}")
    private String clientId;

    @Value("${amadeus.api.secret}")
    private String clientSecret;

    @Override
    public List<Map<String, Object>> searchFlights(FlightSearchRequest req) {
        List<Map<String, Object>> flights = new ArrayList<>();
        offerRepo.deleteAll();

        String token = getAccessToken();
        if (token == null) {
            log.error("Unable to obtain Amadeus access token. Aborting flight search.");
            return flights;
        }
        // Increase WebClient buffer size to handle large responses
        ExchangeStrategies strategies = ExchangeStrategies.builder()
                .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(10 * 1024 * 1024)) // 10 MB
                .build();

        WebClient client = WebClient.builder()
                .baseUrl(baseUrl)
                .defaultHeader("Authorization", "Bearer " + token)
                .exchangeStrategies(strategies)
                .build();

        String uri = String.format(
                "/v2/shopping/flight-offers?originLocationCode=%s&destinationLocationCode=%s&departureDate=%s&adults=%d&travelClass=%s",
                req.getOrigin(), req.getDestination(), req.getDepartDate(), req.getAdults(), req.getCabin()
        );

        Map<String, Object> response = client.get()
                .uri(uri)
                .retrieve()
                .bodyToMono(Map.class)
                .onErrorResume(e -> {
                    log.error("Error calling Amadeus API: {}", e.getMessage());
                    return Mono.empty();
                })
                .block();

        if (response == null || !response.containsKey("data")) {
            log.warn("No flight data returned from Amadeus for {} -> {}", req.getOrigin(), req.getDestination());
            return flights;
        }

        List<Map<String, Object>> data = (List<Map<String, Object>>) response.get("data");
        if (data == null) {
            log.warn("Amadeus response does not contain 'data' field.");
            return flights;
        }

        for (Map<String, Object> offer : data) {
            try {
                Map<String, Object> price = (Map<String, Object>) offer.get("price");
                List<Map<String, Object>> itineraries = (List<Map<String, Object>>) offer.get("itineraries");
                Map<String, Object> firstItinerary = itineraries.get(0);
                List<Map<String, Object>> segments = (List<Map<String, Object>>) firstItinerary.get("segments");
                Map<String, Object> firstSegment = segments.get(0);

                Map<String, Object> departure = (Map<String, Object>) firstSegment.get("departure");
                Map<String, Object> arrival = (Map<String, Object>) firstSegment.get("arrival");

                // Filter by destination
                if (!arrival.get("iataCode").equals(req.getDestination())) continue;

                Map<String, Object> flight = new HashMap<>();
                flight.put("offerId", offer.get("id"));
                flight.put("origin", departure.get("iataCode"));
                flight.put("destination", arrival.get("iataCode"));
                flight.put("price", price.get("total"));
                flight.put("currency", price.get("currency"));
                flight.put("cabin", req.getCabin());
                flights.add(flight);

                FlightOfferCache cache = FlightOfferCache.builder()
                        .offerId((String) flight.get("offerId"))
                        .origin((String) flight.get("origin"))
                        .destination((String) flight.get("destination"))
                        .price(Double.parseDouble(price.get("total").toString()))
                        .currency(price.get("currency").toString())
                        .cabin(req.getCabin())
                        .build();

                offerRepo.save(cache);
            } catch (Exception ex) {
                log.error("Error processing flight offer: {}", ex.getMessage());
            }
        }

        log.info("Retrieved {} flight offers from Amadeus API.", flights.size());
        return flights;
    }

    private String getAccessToken() {
        // Increase buffer size for token request as well
        ExchangeStrategies strategies = ExchangeStrategies.builder()
                .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(2 * 1024 * 1024)) // 2 MB for token
                .build();

        WebClient client = WebClient.builder()
                .baseUrl(baseUrl)
                .exchangeStrategies(strategies)
                .build();

        Map<String, Object> tokenResponse = client.post()
                .uri("/v1/security/oauth2/token")
                .body(BodyInserters
                        .fromFormData("grant_type", "client_credentials")
                        .with("client_id", clientId)
                        .with("client_secret", clientSecret))
                .retrieve()
                .bodyToMono(Map.class)
                .onErrorResume(e -> {
                    log.error("Error fetching Amadeus access token: {}", e.getMessage());
                    return Mono.empty();
                })
                .block();

        if (tokenResponse == null || !tokenResponse.containsKey("access_token")) {
            log.error("Failed to retrieve Amadeus access token. Response: {}", tokenResponse);
            return null;
        }

        return (String) tokenResponse.get("access_token");
    }

    @Override
    public Optional<Map<String, Object>> getOfferById(String offerId) {
        return offerRepo.findById(offerId)
                .map(cache -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("offerId", cache.getOfferId());
                    map.put("origin", cache.getOrigin());
                    map.put("destination", cache.getDestination());
                    map.put("price", cache.getPrice());
                    map.put("currency", cache.getCurrency());
                    map.put("cabin", cache.getCabin());
                    return map;
                });
    }
}