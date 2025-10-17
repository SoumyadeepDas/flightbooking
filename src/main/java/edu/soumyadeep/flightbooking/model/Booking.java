package edu.soumyadeep.flightbooking.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.Instant;
import java.util.List;

@Entity
@Table(name = "bookings")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String bookingReference;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Enumerated(EnumType.STRING)
    private TripType tripType;

    private String origin;
    private String destination;
    private LocalDate departDate;
    private LocalDate returnDate;
    private Double totalPrice;

    private Instant createdAt;

    @OneToMany(mappedBy = "booking", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<Passenger> passengers;

    @PrePersist
    public void prePersist() {
        createdAt = Instant.now();
    }

    public enum TripType {
        ONEWAY, ROUNDTRIP
    }
}
