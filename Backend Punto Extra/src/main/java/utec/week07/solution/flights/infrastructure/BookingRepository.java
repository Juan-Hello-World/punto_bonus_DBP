package utec.week07.solution.flights.infrastructure;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import utec.week07.solution.flights.domain.Booking;
import utec.week07.solution.flights.domain.Flight;
import utec.week07.solution.users.domain.User;

import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByCustomer(User customer);
}
