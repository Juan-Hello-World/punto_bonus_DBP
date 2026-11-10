package utec.week07.solution.flights.domain;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.io.FileOutputStream;
import java.io.IOException;

@Component
public class FlightNotificationService {
    @EventListener
    public void processInventoryUpdate(OnFlightCreated event) throws IOException {
        var email = String.format("""
                               Hello,
                               
                               A new flight was created.
                               
                               Flight Number: %s
                               Airline: %s
                               """, event.getFlight().getFlightNumber(), event.getFlight().getAirlineName());


        try (FileOutputStream fos = new FileOutputStream(String.format("flight_booking_email_%s.txt", event.getFlight().getId()))) {
            fos.write(email.getBytes());
        }
    }
}
