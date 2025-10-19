package edu.soumyadeep.flightbooking.repository;

import edu.soumyadeep.flightbooking.model.FlightOfferCache;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FlightOfferCacheRepository extends JpaRepository<FlightOfferCache, String> {

    /**
     * Find all offers for a specific trip direction (OUTBOUND or RETURN)
     */
    List<FlightOfferCache> findByTripDirection(String tripDirection);

    /**
     * Find all offers for a specific route (origin -> destination)
     */
    List<FlightOfferCache> findByOriginAndDestination(String origin, String destination);

    /**
     * Find a specific offer by ID and trip direction (ensures correct direction)
     */
    Optional<FlightOfferCache> findByOfferIdAndTripDirection(String offerId, String tripDirection);
}