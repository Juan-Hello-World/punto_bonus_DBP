package utec.week07.solution.flights.domain;

import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationEvent;

@Getter
public class OnBookingCreated extends ApplicationEvent {
    private final Booking booking;

    public OnBookingCreated(Object source, Booking booking) {
        super(source);
        this.booking = booking;
    }
}
