package utec.week07.solution.flights.domain;

import org.springframework.context.ApplicationEvent;

public class OnFlightCreated extends ApplicationEvent {
    private final Flight flight;

    public OnFlightCreated(Object source, Flight flight) {
        super(source);
        this.flight = flight;
    }

    public Flight getFlight() {
        return flight;
    }
}
