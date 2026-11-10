package utec.week07.solution.flights.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import utec.week07.solution.users.domain.User;

import java.util.Date;

@Entity
@Setter
@Getter
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "flight_id")
    private Flight flight;
    private Date bookingDate;
    @ManyToOne
    @JoinColumn(name = "customer_id")
    private User customer;
}
