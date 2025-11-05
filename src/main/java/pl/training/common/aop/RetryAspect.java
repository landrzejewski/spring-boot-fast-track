package pl.training.common.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.util.logging.Logger;

@Aspect
@Component
public class RetryAspect {

    private static final Logger LOGGER = Logger.getLogger(RetryAspect.class.getName());

    @Around("@annotation(retry)")
    public Object tryExecute(ProceedingJoinPoint joinPoint, Retry retry) throws Throwable {
        var attempt = 0;
        Throwable throwable;
        do {
            attempt++;
            try {
                return joinPoint.proceed();
            } catch (Throwable currentThrowable) {
                throwable = currentThrowable;
                LOGGER.info("Execution of method \"%s\" failed with exception: %s (attempt: %d)"
                        .formatted(joinPoint.getSignature(), throwable.getClass().getSimpleName(), attempt));
            }
        } while (attempt < retry.attempts());
        throw throwable;
    }

}
