package utec.week07.solution.flights.domain;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class FlightSearchResponseDTO {
    private List<FlightSearchResponseItemDTO> items;
    public FlightSearchResponseDTO(final List<FlightSearchResponseItemDTO> items) {
        this.items = items;
    }
}
