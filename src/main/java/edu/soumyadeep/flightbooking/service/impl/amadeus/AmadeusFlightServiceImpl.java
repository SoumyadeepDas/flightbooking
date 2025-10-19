package edu.soumyadeep.flightbooking.service.impl.amadeus;

import edu.soumyadeep.flightbooking.dto.FlightSearchRequest;
import edu.soumyadeep.flightbooking.repository.FlightOfferCacheRepository;
import edu.soumyadeep.flightbooking.service.FlightService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class AmadeusFlightServiceImpl implements FlightService {

    private static final Logger log = LoggerFactory.getLogger(AmadeusFlightServiceImpl.class);

    private final FlightOfferCacheRepository offerRepo;
    private final AmadeusAuthService authService;
    private final AmadeusApiClient apiClient;
    private final AmadeusFlightMapper mapper;

    @Override
    public List<Map<String, Object>> searchFlights(FlightSearchRequest req) {
        List<Map<String, Object>> allFlights = new ArrayList<>();
        offerRepo.deleteAll();

        String token = authService.getAccessToken();
        if (token == null) return allFlights;

        // OUTBOUND
        var outbound = apiClient.fetchFlights(req.getOrigin(), req.getDestination(),
                req.getDepartDate(), req.getCabin(), token, "OUTBOUND");
        var outboundMapped = mapper.mapFlights(outbound, req.getCabin(), "OUTBOUND");
        saveAll(outboundMapped);
        allFlights.addAll(outboundMapped);

        // RETURN (if applicable)
        if ("ROUNDTRIP".equalsIgnoreCase(req.getTripType().toString()) && req.getReturnDate() != null) {
            var returns = apiClient.fetchFlights(req.getDestination(), req.getOrigin(),
                    req.getReturnDate(), req.getCabin(), token, "RETURN");
            var returnMapped = mapper.mapFlights(returns, req.getCabin(), "RETURN");
            saveAll(returnMapped);
            allFlights.addAll(returnMapped);
        }

        log.info("Retrieved {} total flights ({} outbound, {} return)",
                allFlights.size(), outboundMapped.size(), allFlights.size() - outboundMapped.size());

        return allFlights;
    }

    private void saveAll(List<Map<String, Object>> flights) {
        flights.forEach(f -> offerRepo.save(mapper.toEntity(f)));
    }

    @Override
    public Optional<Map<String, Object>> getOfferById(String offerId) {
        return offerRepo.findById(offerId).map(f -> Map.of(
                "offerId", f.getOfferId(),
                "origin", f.getOrigin(),
                "destination", f.getDestination(),
                "price", f.getPrice(),
                "currency", f.getCurrency(),
                "cabin", f.getCabin(),
                "tripDirection", f.getTripDirection()
        ));
    }
}