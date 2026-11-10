package utec.week07.solution.flights.domain;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class CreateFlightDTO {
    @NotNull
    @NotEmpty
    private String airlineName;
    @NotNull
    @NotEmpty
    @Pattern(regexp = "^[A-Z]{2,3}[0-9]{3}$")
    private String flightNumber;
    @NotNull
    private Date estDepartureTime;
    @NotNull
    private Date estArrivalTime;
    @NotNull
    @Min(1)
    private Integer availableSeats;
}
