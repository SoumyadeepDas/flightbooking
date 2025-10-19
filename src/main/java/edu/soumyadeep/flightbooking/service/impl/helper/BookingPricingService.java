package edu.soumyadeep.flightbooking.service.impl.helper;

import org.springframework.stereotype.Component;
import java.util.Map;

@Component
public class BookingPricingService {

    public double calculateOneWayPrice(Map<String, Object> offer, int passengerCount) {
        double price = ((Number) offer.get("price")).doubleValue();
        return price * passengerCount;
    }

    public double calculateRoundTripPrice(Map<String, Object> outbound, Map<String, Object> returns, int passengerCount) {
        double outboundPrice = ((Number) outbound.get("price")).doubleValue();
        double returnPrice = ((Number) returns.get("price")).doubleValue();
        return (outboundPrice + returnPrice) * passengerCount;
    }
}
