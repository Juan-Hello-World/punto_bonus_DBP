package utec.week07.solution.flights.domain;

import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;

@Component
public class BookingNotificationService implements ApplicationListener<OnBookingCreated> {
        @Override
        public void onApplicationEvent(OnBookingCreated event) {
            try (FileOutputStream fs = new FileOutputStream(String.format("./flight_booking_email_%s.txt", event.getBooking().getId()))) {
                var content = String.format("""
                        Hello %s %s,
                        
                        Your booking was successful!\s
                        
                        The booking is for flight %s with departure date of %s and arrival date of %s.
                        
                        The booking was registered at %s.
                        
                        Bon Voyage!
                        Fly Away Travel
                        """, event.getBooking().getCustomer().getFirstName(), event.getBooking().getCustomer().getLastName(),
                                            event.getBooking().getFlight().getFlightNumber(),
                                            DateTimeFormatter.ISO_INSTANT.format(event.getBooking().getFlight().getEstDepartureTime().toInstant()).replace("Z", ".000+00:00"),
                                            DateTimeFormatter.ISO_INSTANT.format(event.getBooking().getFlight().getEstArrivalTime().toInstant()).replace("Z", ".000+00:00"),
                                            DateTimeFormatter.ISO_INSTANT.format(event.getBooking().getBookingDate().toInstant()).replace("Z", "+00:00"));
                fs.write(content.getBytes());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
}
