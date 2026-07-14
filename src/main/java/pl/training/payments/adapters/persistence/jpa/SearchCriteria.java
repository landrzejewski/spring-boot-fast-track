package pl.training.payments.adapters.persistence.jpa;

public record SearchCriteria(String propertyName, Object value, Matcher matcher) {

    public enum Matcher {

        EQUAL, NOT_EQUAL, START_WITH

    }

}
