package pl.training.payments.adapters;

import org.springframework.stereotype.Component;
import pl.training.payments.application.DateTimeProvider;

import java.time.ZonedDateTime;

@Component
public final class SystemDateTimeProvider implements DateTimeProvider {

    @Override
    public ZonedDateTime getZonedDateTime() {
        return ZonedDateTime.now();
    }

}
