package pl.training.payments.adapters;

import org.springframework.beans.factory.annotation.Value;
import pl.training.common.component.Generator;
import pl.training.payments.application.CardNumberGenerator;
import pl.training.payments.domain.CardNumber;

import java.util.Random;

//@Component
@Generator("rnd")
public final class RandomCardNumberGenerator implements CardNumberGenerator {

    private final Random random = new Random();
    private final int length;

    public RandomCardNumberGenerator(@Value("${card-number-length}") final int length) {
        this.length = length;
    }

    @Override
    public synchronized CardNumber getNext() {
        var number = new StringBuilder();
        for (int index = 0; index < length; index++) {
            int digit = random.nextInt(10);
            number.append(digit);
        }
        return new CardNumber(number.toString());
    }

}
