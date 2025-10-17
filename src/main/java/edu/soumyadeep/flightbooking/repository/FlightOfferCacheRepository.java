package edu.soumyadeep.flightbooking.repository;

import edu.soumyadeep.flightbooking.model.FlightOfferCache;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FlightOfferCacheRepository extends JpaRepository<FlightOfferCache, String> { }
