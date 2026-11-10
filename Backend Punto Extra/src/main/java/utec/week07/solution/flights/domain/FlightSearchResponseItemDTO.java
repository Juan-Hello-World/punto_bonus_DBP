package utec.week07.solution.flights.domain;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Setter
@Getter
public class FlightSearchResponseItemDTO {
    private Long id;
    private String airlineName;
    private String flightNumber;
    private Date estDepartureTime;
    private Date estArrivalTime;
    private Integer availableSeats;
}
