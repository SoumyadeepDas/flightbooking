package edu.soumyadeep.flightbooking.service.impl.helper;

import edu.soumyadeep.flightbooking.dto.BookingRequestDto;
import edu.soumyadeep.flightbooking.model.*;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class BookingMapper {

    public Booking toEntity(BookingRequestDto req, User user, Map<String, Object> offer,
                            double totalPrice, Booking.TripType type) {

        Booking booking = Booking.builder()
                .bookingReference("MMT-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase())
                .user(user)
                .tripType(type)
                .origin((String) offer.get("origin"))
                .destination((String) offer.get("destination"))
                .departDate(req.getDepartDate())
                .returnDate(type == Booking.TripType.ROUNDTRIP ? req.getReturnDate() : null)
                .totalPrice(totalPrice)
                .build();

        List<Passenger> passengers = new ArrayList<>();
        for (BookingRequestDto.PassengerDto dto : req.getPassengers()) {
            TravellerClass travellerClass = TravellerClass.ECONOMY;
            try {
                if (dto.getTravellerClass() != null)
                    travellerClass = TravellerClass.valueOf(dto.getTravellerClass());
            } catch (IllegalArgumentException ignored) {}

            passengers.add(Passenger.builder()
                    .firstName(dto.getFirstName())
                    .lastName(dto.getLastName())
                    .dob(dto.getDob())
                    .travellerClass(travellerClass)
                    .booking(booking)
                    .build());
        }

        booking.setPassengers(passengers);
        return booking;
    }
}
