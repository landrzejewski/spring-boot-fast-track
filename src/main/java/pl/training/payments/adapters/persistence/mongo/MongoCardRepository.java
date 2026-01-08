package pl.training.payments.adapters.persistence.mongo;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.Optional;

public interface MongoCardRepository extends MongoRepository<CardDocument, String> {

    // @Query("{ 'number' : ?0 }")
    Optional<CardDocument> findByNumber(String number);

}
