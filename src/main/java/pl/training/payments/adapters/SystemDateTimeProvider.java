package pl.training.payments.adapters;

import pl.training.payments.application.DateTimeProvider;

import java.time.ZonedDateTime;

public final class SystemDateTimeProvider implements DateTimeProvider {

    @Override
    public ZonedDateTime getZonedDateTime() {
        return ZonedDateTime.now();
    }

}
