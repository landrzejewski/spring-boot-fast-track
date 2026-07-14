package pl.training.payments.adapters.persistence.jpa;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;

import java.util.Set;

public class CardSpecification implements Specification<CardEntity> {

    private final Set<SearchCriteria> searchCriteria;

    public CardSpecification(Set<SearchCriteria> searchCriteria) {
        this.searchCriteria = searchCriteria;
    }

    @Override
    public Predicate toPredicate(Root<CardEntity> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
        return criteriaBuilder.and(searchCriteria.stream().map(criteria -> map(criteria, root, criteriaBuilder))
                        .toArray(Predicate[]::new));
    }

    private Predicate map(SearchCriteria criteria, Root<CardEntity> root, CriteriaBuilder builder) {
        var property = criteria.propertyName();
        var value = criteria.value();
        return switch (criteria.matcher()) {
            case EQUAL -> builder.equal(root.get(property), value);
            case NOT_EQUAL -> builder.notEqual(root.get(property), value);
            case START_WITH -> builder.like(root.get(property), value + "%");
        };
    }

}
