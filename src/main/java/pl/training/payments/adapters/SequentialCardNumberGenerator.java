package pl.training.payments.adapters;

import org.springframework.stereotype.Component;
import pl.training.payments.application.CardNumberGenerator;
import pl.training.payments.domain.CardNumber;

@Component
public class SequentialCardNumberGenerator implements CardNumberGenerator {

    private final int length;

    private long counter;

    public SequentialCardNumberGenerator() {
        this(16);
    }

    public SequentialCardNumberGenerator(final int length) {
        if (length < 0) {
            throw new IllegalArgumentException("Length must be greater than or equal to zero");
        }
        this.length = length;
    }

    @Override
    public synchronized CardNumber getNext() {
        var number = String.format("%0" + length + "d", ++counter);
        return new CardNumber(number);
    }

}
