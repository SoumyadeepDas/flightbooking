package edu.soumyadeep.flightbooking.exception;

public class UserHasNoBookingsException extends RuntimeException {
    public UserHasNoBookingsException(Long userId) {
        super("No bookings found for user ID " + userId);
    }
}
