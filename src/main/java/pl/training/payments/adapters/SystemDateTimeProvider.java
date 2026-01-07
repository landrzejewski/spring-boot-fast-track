package pl.training.payments.adapters;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import pl.training.payments.application.DateTimeProvider;

import java.time.ZonedDateTime;

import static org.springframework.beans.factory.config.BeanDefinition.SCOPE_PROTOTYPE;

// @Profile("dev")
// @Scope(SCOPE_PROTOTYPE)
// @Scope("prototype")
@Component("timeProvider")
// @Service
// @Repository
// @Controller
public final class SystemDateTimeProvider implements DateTimeProvider {

    @Override
    public ZonedDateTime getZonedDateTime() {
        return ZonedDateTime.now();
    }

}
