package utec.week07.solution.flights.domain;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class GetBookingDTO {
    private Long id;
    private Date bookingDate;
    private Long flightId;
    private String flightNumber;
    private String airlineName;
    private Long customerId;
    private String customerFirstName;
    private String customerLastName;
    private Date estDepartureTime;
    private Date estArrivalTime;
}
