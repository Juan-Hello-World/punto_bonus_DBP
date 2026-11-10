package utec.week07.solution;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import utec.week07.solution.flights.domain.CreateFlightDTO;
import utec.week07.solution.flights.domain.FlightService;
import utec.week07.solution.users.domain.UserRegisterDTO;
import utec.week07.solution.users.domain.UserService;
import utec.week07.solution.users.infrastructure.UserRepository;
import utec.week07.solution.flights.infrastructure.FlightRepository;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UserService userService;
    private final FlightService flightService;
    private final UserRepository userRepository;
    private final FlightRepository flightRepository;

    public DataInitializer(UserService userService, FlightService flightService,
                           UserRepository userRepository, FlightRepository flightRepository) {
        this.userService = userService;
        this.flightService = flightService;
        this.userRepository = userRepository;
        this.flightRepository = flightRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        if (userRepository.count() > 0 || flightRepository.count() > 0) return;

        seedUsers();
        seedFlights();
    }

    private void seedUsers() throws Exception {
        record UserSeed(String email, String firstName, String lastName, String password) {}

        var users = List.of(
            new UserSeed("ana.garcia@utec.edu.pe",    "Ana",     "Garcia",    "Password1"),
            new UserSeed("carlos.lopez@utec.edu.pe",  "Carlos",  "Lopez",     "Password1"),
            new UserSeed("maria.torres@utec.edu.pe",  "Maria",   "Torres",    "Password1"),
            new UserSeed("diego.ramirez@utec.edu.pe", "Diego",   "Ramirez",   "Password1"),
            new UserSeed("lucia.flores@utec.edu.pe",  "Lucia",   "Flores",    "Password1")
        );

        for (var u : users) {
            var dto = new UserRegisterDTO();
            dto.setEmail(u.email());
            dto.setFirstName(u.firstName());
            dto.setLastName(u.lastName());
            dto.setPassword(u.password());
            userService.newUser(dto);
        }
    }

    private void seedFlights() throws Exception {
        Instant now = Instant.now();

        record FlightSeed(String airline, String number, long deptHours, long durationHours, int seats) {}

        var flights = List.of(
            new FlightSeed("LATAM Airlines",      "LA101", 48,   5,  180),
            new FlightSeed("LATAM Airlines",      "LA202", 72,   8,  220),
            new FlightSeed("Avianca",             "AV303", 96,   3,  150),
            new FlightSeed("Avianca",             "AV404", 120,  6,  200),
            new FlightSeed("Copa Airlines",       "CM505", 144,  4,  170),
            new FlightSeed("Copa Airlines",       "CM606", 168,  7,  190),
            new FlightSeed("American Airlines",   "AA707", 192,  9,  250),
            new FlightSeed("American Airlines",   "AA808", 216,  5,  230),
            new FlightSeed("Iberia",              "IB909", 240, 11,  300),
            new FlightSeed("Sky Airline",         "SK110", 264,  2,  140),
            new FlightSeed("JetBlue Airways",     "JB211", 288,  6,  180),
            new FlightSeed("Delta Air Lines",     "DL312", 312,  8,  220),
            new FlightSeed("United Airlines",     "UA413", 336,  7,  200),
            new FlightSeed("Air France",          "AF514", 360, 12,  280),
            new FlightSeed("KLM Royal Dutch",     "KL615", 384, 13,  260),
            // Casos para probar errores de reserva desde el frontend:
            new FlightSeed("LATAM Airlines",      "LA100", -48,  5,  180), // vuelo pasado
            new FlightSeed("Sky Airline",         "SK120", 49,   3,  140)  // se superpone con LA101
        );

        for (var f : flights) {
            var dto = new CreateFlightDTO();
            dto.setAirlineName(f.airline());
            dto.setFlightNumber(f.number());
            dto.setEstDepartureTime(Date.from(now.plus(f.deptHours(), ChronoUnit.HOURS)));
            dto.setEstArrivalTime(Date.from(now.plus(f.deptHours() + f.durationHours(), ChronoUnit.HOURS)));
            dto.setAvailableSeats(f.seats());
            flightService.create(dto);
        }
    }
}
