package utec.week07.solution.flights.controllers;

import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import utec.week07.solution.common.NewIdDTO;
import utec.week07.solution.flights.domain.*;

import java.util.Date;
import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/flights")
public class FlightController {

    private final FlightService flightService;
    private ModelMapper modelMapper;

    public FlightController(FlightService flightService, ModelMapper modelMapper) {
        this.flightService = flightService;
        this.modelMapper = modelMapper;
    }

    @PostMapping("/create")
    @PreAuthorize("permitAll()")
    public ResponseEntity<NewIdDTO> create(@Valid @RequestBody CreateFlightDTO dto) throws Exception {
        var newObj = flightService.create(dto);
        return ResponseEntity.created(null).body(new NewIdDTO(newObj.getId()));
    }

    @GetMapping("/{id}")
    @PreAuthorize("permitAll()")
    public ResponseEntity<Flight> findById(@PathVariable long id) {
         return ResponseEntity.ok(flightService.findById(id));
    }

    @GetMapping()
    @PreAuthorize("permitAll()")
    public ResponseEntity<List<Flight>> findAll() {
        return ResponseEntity.ok(flightService.findAll().stream().map(u -> modelMapper.map(u, Flight.class))
                                            .toList());
    }

    @PostMapping("/create-many")
    @PreAuthorize("permitAll()")
    public ResponseEntity<Boolean> createMany(@RequestBody NewFlightManyRequestDTO requestDTO) throws Exception {
        System.out.println("calling");
        flightService.createMany(requestDTO);
        System.out.println("called");
        return ResponseEntity.created(null).build();
    }

    @GetMapping("/search")
    @PreAuthorize("permitAll()")
    public ResponseEntity<FlightSearchResponseDTO> search(@RequestParam(required = false) String flightNumber,
                                                          @RequestParam(required = false) String airlineName,
                                                          @RequestParam(required = false) String estDepartureTimeFrom,
                                                          @RequestParam(required = false) String estDepartureTimeTo) {
        var foundFlights = flightService.search(
                flightNumber,
                airlineName,
                StringUtils.hasText(estDepartureTimeFrom) ? Date.from(Instant.parse(estDepartureTimeFrom)) : null,
                StringUtils.hasText(estDepartureTimeTo) ? Date.from(Instant.parse(estDepartureTimeTo)) : null);
        var foundDTOs = foundFlights.stream().map(f -> modelMapper.map(f, FlightSearchResponseItemDTO.class)).toList();
        return ResponseEntity.ok(new FlightSearchResponseDTO(foundDTOs));
    }

    @GetMapping("/book/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<GetBookingDTO> book(@PathVariable long id) {

        var booking = flightService.getBooking(id);

        var dto = new  GetBookingDTO();
        dto.setId(booking.getId());
        dto.setBookingDate(booking.getBookingDate());
        dto.setFlightId(booking.getFlight().getId());
        dto.setFlightNumber(booking.getFlight().getFlightNumber());
        dto.setAirlineName(booking.getFlight().getAirlineName());
        dto.setEstDepartureTime(booking.getFlight().getEstDepartureTime());
        dto.setEstArrivalTime(booking.getFlight().getEstArrivalTime());
        dto.setCustomerId(booking.getCustomer().getId());
        dto.setCustomerFirstName(booking.getCustomer().getFirstName());
        dto.setCustomerLastName(booking.getCustomer().getLastName());

        return ResponseEntity.ok(dto);
    }

    @PostMapping("/book")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<NewIdDTO> book(@Valid @RequestBody NewBookingDTO dto) throws Exception {
        var newObj = flightService.book(dto);
        return ResponseEntity.ok(new NewIdDTO(newObj.getId()));
    }
}
