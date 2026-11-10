package utec.week07.solution.flights.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import utec.week07.solution.common.ValidationException;

import java.util.Date;

@Entity
@Setter
@Getter
public class Flight {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String airlineName;
    private String flightNumber;
    private Date estDepartureTime;
    private Date estArrivalTime;
    private Integer availableSeats;

    public void reduceAvailableSeats(Integer delta) throws ValidationException {
        if (availableSeats - delta < 0) {
            throw new ValidationException("Cannot reduce available seats less than zero");
        }

        availableSeats -= delta;
    }
}
