package pl.training.payments.adapters.persistence.jpa;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

import static pl.training.payments.adapters.persistence.jpa.SearchCriteria.Matcher.EQUAL;
import static pl.training.payments.adapters.persistence.jpa.SearchCriteria.Matcher.START_WITH;

@Transactional
@Order(2)
// @Component
public class JpaExamples implements ApplicationRunner {

    private final JpaCardRepository repository;

    public JpaExamples(JpaCardRepository repository) {
        this.repository = repository;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        /*repository.findAllProjections()
                .forEach(cardProjection -> System.out.println(cardProjection.getInfo()));*/

       /* var result = repository.findAllAsync();
        System.out.println(result.isDone());
        result.get()
                .forEach(System.out::println);*/

        /*var exampleEntity = new CardEntity();
        exampleEntity.setCurrencyCode("PLN");

        var matcher = ExampleMatcher.matching()
                .withIgnoreNullValues()
                .withIgnoreCase()
                .withIgnorePaths("properties");

        repository.findAll(Example.of(exampleEntity, matcher))
                .forEach(System.out::println);*/

        var searchCriteria = Set.of(
                new SearchCriteria("number", "40711", START_WITH),
                new SearchCriteria("currencyCode", "PLN", EQUAL)
        );
        repository.findAll(new CardSpecification(searchCriteria))
                .forEach(System.out::println);
    }

}
