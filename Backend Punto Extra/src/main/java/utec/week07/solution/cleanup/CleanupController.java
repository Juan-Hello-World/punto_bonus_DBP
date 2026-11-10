package utec.week07.solution.cleanup;

import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import utec.week07.solution.flights.domain.FlightService;
import utec.week07.solution.users.domain.UserService;

@RestController
@RequestMapping("/cleanup")
class CleanupController {
    private UserService userService;
    private FlightService flightService;

    CleanupController(UserService userService, FlightService flightService) {
        this.userService = userService;
        this.flightService = flightService;
    }

    @DeleteMapping()
    @PreAuthorize("permitAll()")
    public ResponseEntity cleanup() {
        flightService.deleteAll();
        userService.deleteAll();
        return ResponseEntity.ok().build();
    }
}