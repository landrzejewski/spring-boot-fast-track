package pl.training.payments.application;

import pl.training.payments.domain.CardNumber;

public interface CardNumberGenerator {

    CardNumber getNext();

}
