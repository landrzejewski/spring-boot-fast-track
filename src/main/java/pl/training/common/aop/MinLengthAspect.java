package pl.training.common.aop;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

import static pl.training.common.aop.AopHelpers.applyArgumentOperator;

@Aspect
@Component
public class MinLengthAspect {

    @Before("execution(* *(@pl.training.common.aop.MinLength (*)))")
    public void validate(JoinPoint joinPoint) throws NoSuchMethodException {
        applyArgumentOperator(joinPoint, MinLength.class, (String argument, MinLength minLength) -> {
            if (argument.length() < minLength.value()) {
                throw new IllegalArgumentException("Value is too short, minimum length is: " + minLength.value());
            }
        });
    }

}
