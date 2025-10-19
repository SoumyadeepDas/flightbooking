package edu.soumyadeep.flightbooking.constant;

import lombok.NoArgsConstructor;

@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public final class EndpointConstant {
    public static final String USER_PATH = "/api/v1/users";
    public static final String FLIGHT_PATH = "/api/v1/flights";
    public static final String FLIGHT_SEARCH = "/search";
    public static final String BOOKING_PATH = "/api/v1/bookings";
    public static final String BOOKING_ONEWAY = "/oneway";
    public static final String BOOKING_ROUNDTRIP = "/roundtrip";
    public static final String FIND_USER_BY_USERID = "/{id}";
    public static final String FIND_BOOKING_BY_BOOKING_ID = "/{id}";
    public static final String FIND_ALL_BOOKINGS_BY_USER_ID = "/user/{userId}";
    public static final String FIND_BOOKING_BY_BOOKING_REF = "/reference/{ref}";
}
