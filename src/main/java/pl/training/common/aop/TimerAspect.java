package pl.training.common.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.context.annotation.Profile;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import pl.training.common.aop.Timer.UnitType;

import java.util.logging.Logger;

// @Order(2)
@Profile("dev")
@Aspect
@Component
public class TimerAspect implements Ordered {

    private static final Logger LOGGER = Logger.getLogger(TimerAspect.class.getName());

    @Around("@annotation(timer)")
    public Object measure(ProceedingJoinPoint joinPoint, Timer timer) throws Throwable {
        var units = timer.unitType();
        var startTime = getTime(units);
        var result = joinPoint.proceed();
        var endTime = getTime(units);
        var totalTime = endTime - startTime;
        LOGGER.info("Method %s executes in %d %s".formatted(joinPoint.getSignature(), totalTime, units.name().toLowerCase()));
        return result;
    }

    private long getTime(UnitType unitType) {
        return unitType == UnitType.MS ? System.currentTimeMillis() : System.nanoTime();
    }

    @Override
    public int getOrder() {
        return 2;
    }

}
