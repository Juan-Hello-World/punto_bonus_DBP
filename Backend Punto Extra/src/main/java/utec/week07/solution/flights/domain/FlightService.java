package utec.week07.solution.flights.domain;

import ch.qos.logback.core.util.StringUtil;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.antlr.v4.runtime.misc.LogManager;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ResponseStatusException;
import utec.week07.solution.common.ValidationException;
import utec.week07.solution.flights.infrastructure.BookingRepository;
import utec.week07.solution.flights.infrastructure.FlightRepository;
import utec.week07.solution.users.domain.User;
import utec.week07.solution.users.domain.UserRegisterDTO;
import utec.week07.solution.users.domain.UserService;
import utec.week07.solution.users.infrastructure.UserRepository;


import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Locale;

@Service
public class FlightService implements ApplicationEventPublisherAware {
    private final FlightRepository flightRepository;
    private final UserService userService;
    private final ModelMapper modelMapper;
    private final BookingRepository bookingRepository;
    private ApplicationEventPublisher publisher;

    public FlightService(FlightRepository flightRepository, UserService userService, ModelMapper modelMapper,
                         BookingRepository bookingRepository) {
        this.flightRepository = flightRepository;
        this.userService = userService;
        this.modelMapper = modelMapper;
        this.bookingRepository = bookingRepository;
    }

    public Flight findById(long id) {
        return this.flightRepository.findById(id).orElseThrow();
    }

    public Flight create(CreateFlightDTO dto) throws Exception {
        var found = this.flightRepository.findByFlightNumber(dto.getFlightNumber());
        if (found.isPresent()) {
            throw new ValidationException("Flight already exists");
        }

        var flight = new Flight();
        modelMapper.map(dto, flight);
        this.flightRepository.save(flight);
        return flight;
    }

    public List<Flight> findAll() {
        return flightRepository.findAll();
    }

    @Async
    public void createMany(NewFlightManyRequestDTO newFlights) throws Exception {
        for (CreateFlightDTO flight: newFlights.inputs) {
            this.create(flight);
        }

        System.out.println("Flights created");
    }

    public List<Flight> search(String flightNumber, String airlineName, Date estDepartureTimeFrom,
                               Date estDepartureTimeTo) {

        return this.flightRepository.findAll().stream().filter(f -> {
            var found = true;
            if (StringUtils.hasText(flightNumber)) {
                found &= f.getFlightNumber().toLowerCase(Locale.ROOT).contains(flightNumber.toLowerCase(Locale.ROOT));
            }

            if (StringUtils.hasText(airlineName)) {
                found &= f.getAirlineName().toLowerCase(Locale.ROOT).contains(airlineName.toLowerCase(Locale.ROOT));
            }

            if (estDepartureTimeFrom != null) {
                found &= f.getEstDepartureTime().getTime() >= estDepartureTimeFrom.getTime();
            }

            if (estDepartureTimeTo != null) {
                found &= f.getEstDepartureTime().getTime() <= estDepartureTimeTo.getTime();
            }

            return found;
        }).toList();
    }

    public Booking getBooking(long id) {
        return this.bookingRepository.findById(id).orElseThrow();
    }

    private void validateBookingsOverlapping(User currentUser, Flight pivotFlight) throws Exception {
        // get other booking flights for the same user
        var sameUserFlights = this.bookingRepository.findByCustomer(currentUser).stream().map(b -> b.getFlight())
                                             .filter(f -> !f.getId().equals(pivotFlight.getId()))
                                             .toList();

        var overlapping = sameUserFlights.stream().filter(f -> pivotFlight.getEstDepartureTime().getTime() < f.getEstArrivalTime().getTime() &&
                pivotFlight.getEstArrivalTime().getTime() > f.getEstDepartureTime().getTime()).count() > 0;

        if (overlapping) {
            throw new ValidationException("Overlapping flight found");
        }
    }

    @Transactional
    public Booking book(NewBookingDTO dto) throws Exception {
        var flight = this.flightRepository.findById(dto.getFlightId()).orElseThrow();

        var now = Instant.now().toEpochMilli();
        if (flight.getEstDepartureTime().getTime() < now || flight.getEstArrivalTime().getTime() < now) {
            throw new ValidationException("Cannot book a past flight");
        }

        var currentUser = (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        validateBookingsOverlapping(currentUser, flight);

        Booking booking = new Booking();
        booking.setBookingDate(new Date());
        booking.setFlight(flight);
        booking.setCustomer(userService.findById(currentUser.getId()));
        this.bookingRepository.save(booking);

        flight.reduceAvailableSeats(1);
        this.flightRepository.save(flight);

        this.publisher.publishEvent(new OnBookingCreated(this, booking));
        return booking;
    }

    public void deleteAll() {
        this.bookingRepository.deleteAll();
        this.flightRepository.deleteAll();
    }

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.publisher = applicationEventPublisher;
    }
}
