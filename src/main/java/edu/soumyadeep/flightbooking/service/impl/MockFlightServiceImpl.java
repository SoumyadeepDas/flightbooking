// Mock class commented out; replaced by AmadeusFlightServiceImpl for live data integration.
/*

package edu.soumyadeep.flightbooking.service.impl;

import edu.soumyadeep.flightbooking.dto.FlightSearchRequest;
import edu.soumyadeep.flightbooking.model.FlightOfferCache;
import edu.soumyadeep.flightbooking.repository.FlightOfferCacheRepository;
import edu.soumyadeep.flightbooking.service.FlightService;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class MockFlightServiceImpl implements FlightService {

    private final FlightOfferCacheRepository offerRepo;

    public MockFlightServiceImpl(FlightOfferCacheRepository offerRepo) {
        this.offerRepo = offerRepo;
    }

    @Override
    public List<Map<String, Object>> searchFlights(FlightSearchRequest req) {
        List<Map<String, Object>> flights = new ArrayList<>();

        // clear old mock data for demo purposes
        offerRepo.deleteAll();

        for (int i = 1; i <= 3; i++) {
            String offerId = UUID.randomUUID().toString();
            double price = 3000 + i * 500;

            // persist offer
            FlightOfferCache cache = FlightOfferCache.builder()
                    .offerId(offerId)
                    .origin(req.getOrigin())
                    .destination(req.getDestination())
                    .price(price)
                    .currency("INR")
                    .cabin(req.getCabin() == null ? "ECONOMY" : req.getCabin())
                    .build();
            offerRepo.save(cache);

            Map<String, Object> offer = new HashMap<>();
            offer.put("offerId", offerId);
            offer.put("origin", req.getOrigin());
            offer.put("destination", req.getDestination());
            offer.put("price", price);
            offer.put("currency", "INR");
            offer.put("cabin", cache.getCabin());
            flights.add(offer);
        }
        return flights;
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
 */